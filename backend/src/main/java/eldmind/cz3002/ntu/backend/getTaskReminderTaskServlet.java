package eldmind.cz3002.ntu.backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eldmind.cz3002.ntu.backend.model.TaskReminder;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by n on 11/3/2017.
 */

public class getTaskReminderTaskServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        long elderlyPhoneNumber = (long)inputJO.getInt("elderly_phoneNumber");
        String status = "ENABLED";
        List<TaskReminder> list = ofy().load().type(TaskReminder.class).filter("phoneNum",elderlyPhoneNumber).filter("status",status).list();
        JSONObject respJO = new JSONObject();
        JSONArray ja = new JSONArray();
        for(int i=0;i<list.size();i++){
            ja.put(list.get(i).toJSON());
        }
        respJO.put("reminders",ja);
        resp.getWriter().print(respJO.toString());
    }
}
