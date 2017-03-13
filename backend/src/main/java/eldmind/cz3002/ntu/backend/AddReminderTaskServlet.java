package eldmind.cz3002.ntu.backend;

import com.googlecode.objectify.Key;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

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

public class AddReminderTaskServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        Key<User> key = Key.create(User.class,inputJO.getLong("phoneNumber"));
        JSONObject respJO = new JSONObject();
        User e = ofy().load().key(key).now();
        if(e!=null) {
            int id = inputJO.getInt("_id");
            if(id==-1){
                id = getLatestID(inputJO.getLong("phoneNumber"))+1;
            }
            TaskReminder tr = new TaskReminder();
            tr.setId(inputJO.getLong("phoneNumber") + " " + id);
            tr.setPhoneNum(inputJO.getLong("phoneNumber"));
            tr.setTitle(inputJO.getString("title"));
            tr.setDesc(inputJO.getString("desc"));
            tr.setReccurring(inputJO.getString("recurring"));
            if(tr.getReccurring().equals("SINGLE")){
                tr.setDueTime(inputJO.getLong("dueTime"));
                tr.setWeeklyDay("");
                tr.setWeeklyTime("");
            }else{
                tr.setDueTime(0);
                tr.setWeeklyDay(inputJO.getString("weeklyDay"));
                tr.setWeeklyTime(inputJO.getString("weeklyTime"));
            }
            tr.setStatus(inputJO.getString("status"));

            ofy().save().entity(tr).now();
            respJO.put("success", true);
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
                data.put("task",tr.toJSON());
                data.put("by",inputJO.getLong("CphoneNumber"));
                data.put("action","create");
                jo.put("to",token);
                jo.put("data",data);
                StringEntity se = new StringEntity(jo.toString());
                request.setEntity(se);
                HttpResponse FCM_response = client.execute(request);
            }
        }else{
            respJO.put("success", false);
            respJO.put("msg", "Phone number does not exist.");
        }

        resp.getWriter().println(respJO.toString());
    }
    public int getLatestID(long phoneNum){
        List<TaskReminder> list = ofy().load().type(TaskReminder.class)
                .filter("phoneNum",phoneNum).order("__key__")
                .filter("status","ENABLED").list();
        if(list.size()!=0){
            return Integer.parseInt(list.get(list.size()-1).getId().split(" ")[1]);
        }else{
            return 0;
        }
    }
}
