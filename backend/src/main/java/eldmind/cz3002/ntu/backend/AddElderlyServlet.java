package eldmind.cz3002.ntu.backend;

import com.googlecode.objectify.Key;

import org.json.JSONObject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eldmind.cz3002.ntu.backend.model.Elderly;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by n on 26/2/2017.
 */

public class AddElderlyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //how to save into datastore
        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        long phoneNumber = inputJO.getLong("phoneNumber");//Long.parseLong(req.getParameter("phoneNumber"));
        String firebaseToken = inputJO.getString("token");//req.getParameter("token");

        Key<Elderly> key = Key.create(Elderly.class,phoneNumber);
        Elderly e = ofy().load().key(key).now();
        if(e==null){
            //create
            e = new Elderly();
            e.setPhoneNumber(phoneNumber);
            e.setFirebaseToken(firebaseToken);
            // Use Objectify to save the greeting and now() is used to make the call synchronously as we
            // will immediately get a new page using redirect and we want the data to be present.
            ofy().save().entity(e).now();
            JSONObject respJO = new JSONObject();
            respJO.put("success",true);
            respJO.put("phoneNumber",phoneNumber);
            respJO.put("token",firebaseToken);
            resp.getWriter().println(respJO.toString());
        }else{
            //update
            e.setPhoneNumber(phoneNumber);
            e.setFirebaseToken(firebaseToken);
            // Use Objectify to save the greeting and now() is used to make the call synchronously as we
            // will immediately get a new page using redirect and we want the data to be present.
            ofy().save().entity(e).now();
            JSONObject respJO = new JSONObject();
            respJO.put("success",true);
            respJO.put("phoneNumber",phoneNumber);
            respJO.put("token",firebaseToken);
            resp.getWriter().println(respJO.toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //how to save into datastore
        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        long phoneNumber = inputJO.getLong("phoneNumber");//Long.parseLong(req.getParameter("phoneNumber"));
        String firebaseToken = inputJO.getString("token");//req.getParameter("token");

        Key<Elderly> key = Key.create(Elderly.class,phoneNumber);
        Elderly e = ofy().load().key(key).now();
        if(e==null){
            //create
            e = new Elderly();
            e.setPhoneNumber(phoneNumber);
            e.setFirebaseToken(firebaseToken);
            // Use Objectify to save the greeting and now() is used to make the call synchronously as we
            // will immediately get a new page using redirect and we want the data to be present.
            ofy().save().entity(e).now();
            JSONObject respJO = new JSONObject();
            respJO.put("success",true);
            respJO.put("phoneNumber",phoneNumber);
            respJO.put("token",firebaseToken);
            resp.getWriter().println(respJO.toString());
        }else{
            //update
            e.setPhoneNumber(phoneNumber);
            e.setFirebaseToken(firebaseToken);
            // Use Objectify to save the greeting and now() is used to make the call synchronously as we
            // will immediately get a new page using redirect and we want the data to be present.
            ofy().save().entity(e).now();
            JSONObject respJO = new JSONObject();
            respJO.put("success",true);
            respJO.put("phoneNumber",phoneNumber);
            respJO.put("token",firebaseToken);
            resp.getWriter().println(respJO.toString());
        }
    }
}
