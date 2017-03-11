package eldmind.cz3002.ntu.backend;

import com.googlecode.objectify.Key;

import org.json.JSONObject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eldmind.cz3002.ntu.backend.model.TaskReminder;
import eldmind.cz3002.ntu.backend.model.User;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by n on 10/3/2017.
 */

public class AddReminderTaskServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print("asd1");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        Key<User> key = Key.create(User.class,inputJO.getLong("phoneNumber"));
        User e = ofy().load().key(key).now();
        if(e!=null) {
            TaskReminder tr = new TaskReminder();
            tr.setId(inputJO.getLong("phoneNumber") + " " + inputJO.getInt("_id"));
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
            JSONObject respJO = new JSONObject();
            respJO.put("success", true);
            resp.getWriter().println(respJO.toString());
        }else{
            JSONObject respJO = new JSONObject();
            respJO.put("success", false);
            respJO.put("msg", "Phone number does not exist.");
            resp.getWriter().println(respJO.toString());
        }
    }
}
