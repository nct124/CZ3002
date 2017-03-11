package eldmind.cz3002.ntu.backend;

import com.googlecode.objectify.Key;

import org.json.JSONObject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eldmind.cz3002.ntu.backend.model.TaskReminder;

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
        String keyid = inputJO.getLong("phoneNumber") + " " + inputJO.getInt("_id");
        Key<TaskReminder> key = Key.create(TaskReminder.class,keyid);
        TaskReminder e = ofy().load().key(key).now();
        if(e!=null) {
            if(inputJO.getString("status").equals("DISABLED")){
                e.setStatus(inputJO.getString("status"));
                ofy().save().entity(e).now();
                JSONObject respJO = new JSONObject();
                respJO.put("success", true);
                resp.getWriter().println(respJO.toString());
            }else{
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
                JSONObject respJO = new JSONObject();
                respJO.put("success", true);
                resp.getWriter().println(respJO.toString());
            }
        }else{
            JSONObject respJO = new JSONObject();
            respJO.put("success", false);
            respJO.put("msg", "Task does not exist.");
            resp.getWriter().println(respJO.toString());
        }
    }
}
