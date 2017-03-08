/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package eldmind.cz3002.ntu.backend;

import com.googlecode.objectify.ObjectifyService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eldmind.cz3002.ntu.backend.model.User;
import eldmind.cz3002.ntu.backend.others.GAEConstants;

public class AddCustodeeServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String output = "";
        Boolean found = false;
        List<User> list = ObjectifyService.ofy().load().type(User.class).list();

        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        long phoneNumber = inputJO.getLong("phoneNumber");//Long.parseLong(req.getParameter("phoneNumber"));
        String firebaseToken = inputJO.getString("token");//req.getParameter("token");
        //long phoneNumber = 98368611;
        User e1 = null;
        for (int i = 0; i < list.size(); i++) {
            e1 = list.get(i);
            if (e1.getPhoneNumber() == phoneNumber) {
                found = true;
                break;
            }
        }

        if (!found) {
            //TODO custodian not found return
        }

        //Send custodian notification
        output += "ZHIJIE Trial test   ==>" + e1.getFirebaseToken() + "<br/>";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(GAEConstants.FCM_URL);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "key=" + GAEConstants.AUTHORIZATION_KEY);
        JSONObject jo = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("message", "Someone wants to add you as Custodian!");
        jo.put("to", e1.getFirebaseToken());
        jo.put("data", data);
        StringEntity se = new StringEntity(jo.toString());
        request.setEntity(se);
        HttpResponse response = client.execute(request);
        resp.getWriter().println(output);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("text/plain");
        String output = "";
        Boolean found = false;
        List<User> list = ObjectifyService.ofy().load().type(User.class).list();

        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        long phoneNumber = inputJO.getLong("phoneNumber");//Long.parseLong(req.getParameter("phoneNumber"));
        //String firebaseToken = inputJO.getString("token");//req.getParameter("token");
        //long phoneNumber = 98368611;
        User e1 = null;
        for (int i = 0; i < list.size(); i++) {
            e1 = list.get(i);
            if (e1.getPhoneNumber() == phoneNumber) {
                found = true;
                break;
            }
        }

        if (!found) {
            //TODO custodian not found return
        }

        //Send custodian notification
        output += "ZHIJIE Trial test   ==>" + e1.getFirebaseToken() + "<br/>";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(GAEConstants.FCM_URL);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "key=" + GAEConstants.AUTHORIZATION_KEY);
        JSONObject jo = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("message", "Someone wants to add you as Custodian!");
        jo.put("to", e1.getFirebaseToken());
        jo.put("data", data);
        StringEntity se = new StringEntity(jo.toString());
        request.setEntity(se);
        HttpResponse response = client.execute(request);
        resp.getWriter().println(output);

    }
}
