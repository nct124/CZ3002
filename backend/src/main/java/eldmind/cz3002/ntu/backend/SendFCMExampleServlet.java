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

import eldmind.cz3002.ntu.backend.model.Elderly;
import eldmind.cz3002.ntu.backend.others.GAEConstants;

public class SendFCMExampleServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        //how to send FCM notification
        /*String token = "cWe_NczBcCw:APA91bGIZWT8tXDlHMyk_m2CYqj73uAUfuQqulCKjL7c-qqiddr8L2HBiz38Oh20vtMQpd7SICWdBgTgsWMy5t9QfwWm7ZFbifTeRVYv9a2XSW0b6RLfPHuRk4QF1d-SFxwIopfJ1awf";
        resp.setContentType("text/plain");
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(FCM_URL);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization","key="+AUTHORIZATION_KEY);
        JSONObject jo = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("message","hi");
        jo.put("to",token);
        jo.put("data",data);
        StringEntity se = new StringEntity(jo.toString());
        request.setEntity(se);
        HttpResponse response = client.execute(request);
        String responseString = new BasicResponseHandler().handleResponse(response);
        resp.getWriter().println(responseString);*/

        String output = "";
        List<Elderly> list = ObjectifyService.ofy().load().type(Elderly.class).list();
        for(int i=0;i<list.size();i++){
            Elderly e1 = list.get(i);
            output+=e1.getFirebaseToken()+"<br/>";
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(GAEConstants.FCM_URL);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","key="+GAEConstants.AUTHORIZATION_KEY);
            JSONObject jo = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("message","hi2");
            jo.put("to",e1.getFirebaseToken());
            jo.put("data",data);
            StringEntity se = new StringEntity(jo.toString());
            request.setEntity(se);
            HttpResponse response = client.execute(request);
        }
        resp.getWriter().println(output);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("text/plain");
    }
}
