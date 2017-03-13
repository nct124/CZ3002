package eldmind.cz3002.ntu.backend;

import com.googlecode.objectify.Key;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eldmind.cz3002.ntu.backend.model.TaskReminder;
import eldmind.cz3002.ntu.backend.model.User;
import eldmind.cz3002.ntu.backend.others.GAEConstants;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by n on 10/3/2017.
 */

public class UpdateReminderTaskServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        JSONObject respJO = new JSONObject();
        String keyid = inputJO.getLong("phoneNumber") + " " + inputJO.getInt("_id");
        Key<TaskReminder> key = Key.create(TaskReminder.class,keyid);
        TaskReminder e = ofy().load().key(key).now();

        if(e!=null) {
            String action = "";
            if(inputJO.getString("status").equals("DISABLED")){//delete
                e.setStatus(inputJO.getString("status"));
                ofy().save().entity(e).now();
                respJO.put("success", true);
                action = "delete";
            }else{//update
                e.setTitle(inputJO.getString("title"));
                e.setDesc(inputJO.getString("desc"));
                e.setReccurring(inputJO.getString("recurring"));
                if(e.getReccurring().equals("SINGLE")){
                    e.setDueTime(inputJO.getLong("dueTime"));
                    e.setWeeklyDay("");
                    e.setWeeklyTime("");
                }else{
                    e.setDueTime(0);
                    e.setWeeklyDay(inputJO.getString("weeklyDay"));
                    e.setWeeklyTime(inputJO.getString("weeklyTime"));
                }
                e.setStatus(inputJO.getString("status"));
                ofy().save().entity(e).now();
                respJO.put("success", true);
                action="update";
            }
            if(inputJO.getLong("CphoneNumber")!=0){//send FCM to elderly
                Key<User> keyE = Key.create(User.class,inputJO.getLong("phoneNumber"));
                User elderly = ofy().load().key(keyE).now();
                String token = elderly.getFirebaseToken();

                HttpClient client = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost(GAEConstants.FCM_URL);
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Authorization","key="+GAEConstants.AUTHORIZATION_KEY);
                JSONObject jo = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("task",e.toJSON());
                data.put("by",inputJO.getLong("CphoneNumber"));
                data.put("action",action);
                jo.put("to",token);
                jo.put("data",data);
                StringEntity se = new StringEntity(jo.toString());
                request.setEntity(se);
                HttpResponse FCM_response = client.execute(request);
            }
        }else{
            respJO.put("success", false);
            respJO.put("msg", "Task does not exist.");
        }
        resp.getWriter().println(respJO.toString());
    }
}
