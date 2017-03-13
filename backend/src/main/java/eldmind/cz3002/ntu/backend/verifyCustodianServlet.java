package eldmind.cz3002.ntu.backend;

import com.googlecode.objectify.Key;

import org.json.JSONObject;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eldmind.cz3002.ntu.backend.model.Custodian_Elderly_Relation;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by n on 11/3/2017.
 */

public class verifyCustodianServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {
        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        long caregiver_phoneNumber = inputJO.getLong("caregiver_phoneNumber");
        long elderly_phoneNumber = inputJO.getLong("elderly_phoneNumber");
        String verifyCode = inputJO.getString("verify_code");
        String msg = "";
        String keyid = caregiver_phoneNumber+" "+elderly_phoneNumber;
        Key<Custodian_Elderly_Relation> key = Key.create(Custodian_Elderly_Relation.class,keyid);
        Custodian_Elderly_Relation e = ofy().load().key(key).now();
        if(e!=null){
            if(e.getStatus().equals("PENDING")){
                if(e.getVerifyCode().equals(verifyCode)){//match
                    msg = "You are now allowed to manage reminders for the elderly";
                    e.setStatus("CONFIRMED");
                    ofy().save().entity(e).now();
                    JSONObject respJO = new JSONObject();
                    respJO.put("success",true);
                    respJO.put("msg",msg);
                    resp.getWriter().println(respJO.toString());
                }else{
                    msg = "Verification code does not match.";
                    JSONObject respJO = new JSONObject();
                    respJO.put("success",false);
                    respJO.put("msg",msg);
                    resp.getWriter().println(respJO.toString());
                }
            }else{
                msg = "You already have the rights.";
                JSONObject respJO = new JSONObject();
                respJO.put("success",false);
                respJO.put("msg",msg);
                resp.getWriter().println(respJO.toString());
            }
        }else{
            msg = "Record does not exist.";
            JSONObject respJO = new JSONObject();
            respJO.put("success",false);
            respJO.put("msg",msg);
            resp.getWriter().println(respJO.toString());
        }

    }
}
