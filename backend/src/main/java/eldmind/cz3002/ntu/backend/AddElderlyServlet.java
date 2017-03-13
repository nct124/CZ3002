/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package eldmind.cz3002.ntu.backend;

import com.googlecode.objectify.Key;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eldmind.cz3002.ntu.backend.model.Custodian_Elderly_Relation;
import eldmind.cz3002.ntu.backend.model.User;
import eldmind.cz3002.ntu.backend.others.GAEConstants;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class AddElderlyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {
        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        long caregiver_phoneNumber = inputJO.getLong("caregiver_phoneNumber");
        long elderly_phoneNumber = inputJO.getLong("elderly_phoneNumber");
        boolean need = true;//indicate if verification is needed
        //retrieve to get firebase token
        Key<User> keyE = Key.create(User.class,elderly_phoneNumber);
        User elderly = ofy().load().key(keyE).now();
        String token = elderly.getFirebaseToken();

        Random rnd = new Random();
        String verifyCode = Integer.toString((100000 + rnd.nextInt(900000)));
        String keyid = caregiver_phoneNumber+" "+elderly_phoneNumber;
        Key<Custodian_Elderly_Relation> key = Key.create(Custodian_Elderly_Relation.class,keyid);
        Custodian_Elderly_Relation e = ofy().load().key(key).now();
        String status = "PENDING";
        String msg = "";
        if(e==null){//create
            msg = "Requested.. Get verification code from the elderly";
            e = new Custodian_Elderly_Relation(keyid,caregiver_phoneNumber,elderly_phoneNumber,verifyCode,"PENDING");
            ofy().save().entity(e).now();
        }else{//update
            if(e.getStatus().equals(status)){
                msg = "New verification code requested.. Get verification code from the elderly";
                e.setVerifyCode(verifyCode);
                ofy().save().entity(e).now();
            }else{//if status is alrdy CONFIRMED
                msg = "It is already verified";
                need = false;
            }
        }
        if(need){
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(GAEConstants.FCM_URL);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","key="+GAEConstants.AUTHORIZATION_KEY);
            JSONObject jo = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("message","VerifyCode: "+verifyCode);
            data.put("action","verify");
            jo.put("to",token);
            jo.put("data",data);
            StringEntity se = new StringEntity(jo.toString());
            request.setEntity(se);
            HttpResponse FCM_response = client.execute(request);
            String responseString = new BasicResponseHandler().handleResponse(FCM_response);
            JSONObject FCM_jo = new JSONObject(responseString);
            //if(FCM_jo.getBoolean("success")){
            JSONObject respJO = new JSONObject();
            respJO.put("success",true);
            respJO.put("msg",msg);
            resp.getWriter().println(respJO.toString());
            /*}else{
                msg = "SERVER ERROR";
                JSONObject respJO = new JSONObject();
                respJO.put("success",false);
                respJO.put("msg",msg);
                resp.getWriter().println(respJO.toString());
            }*/
        }else{
            JSONObject respJO = new JSONObject();
            respJO.put("success",false);
            respJO.put("msg",msg);
            resp.getWriter().println(respJO.toString());
        }
    }
}
