package eldmind.cz3002.ntu.backend;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eldmind.cz3002.ntu.backend.model.Custodian_Elderly_Relation;
import eldmind.cz3002.ntu.backend.others.GAEConstants;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by n on 7/3/2017.
 */

public class TrialServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Random rnd = new Random();

        JSONObject inputJO = new JSONObject(req.getParameter("data"));
        long custodianNum = inputJO.getLong("CustodianNum");
        long elderlyNum = inputJO.getLong("ElderlyNum");
        String id = custodianNum+" "+elderlyNum;
        String verifyCode = Integer.toString((100000 + rnd.nextInt(900000)));
        String status = "PENDING";

        Custodian_Elderly_Relation cer = new Custodian_Elderly_Relation(id,custodianNum,elderlyNum,verifyCode,status);
        ofy().save().entity(cer).now();
        //send FCM with verifyCode
        String token = "cmE5nRWtSD8:APA91bEGNvTsyGqjfv1X5RqpvRrpDSwobvnLB8v2DwXS839Y-E_MKsvcPnbhEQXh_WW93BgP9zfg1nYU6rYqInqD1SWsxRZgABCHLgP9bIRdXXmPD-6MOrI3j_ggICqWSiiGlDQf6Wk9";
        //FCM
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(GAEConstants.FCM_URL);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization","key="+GAEConstants.AUTHORIZATION_KEY);
        JSONObject jo = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("message","VerifyCode: "+verifyCode);
        jo.put("to",token);
        jo.put("data",data);
        StringEntity se = new StringEntity(jo.toString());
        request.setEntity(se);
        HttpResponse response = client.execute(request);

        JSONObject respJO = new JSONObject();
        respJO.put("success",true);
        respJO.put("custodianNum",custodianNum);
        respJO.put("elderlyNum",elderlyNum);
        respJO.put("id",id);
        respJO.put("verifyCode",verifyCode);
        respJO.put("status",status);
        resp.getWriter().println(respJO.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
