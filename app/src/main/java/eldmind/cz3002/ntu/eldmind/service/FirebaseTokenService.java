package eldmind.cz3002.ntu.eldmind.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.UserDataSource;
import eldmind.cz3002.ntu.eldmind.model.User;

/**
 * Created by n on 22/2/2017.
 */

public class FirebaseTokenService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    UserDataSource datasource;
    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        boolean registered = false;

        datasource = new UserDataSource(this);
        datasource.open();
        User e = null;
        List<User> list = datasource.getAllUser();
        if(list.size()>0){
            e = list.get(0);
            if(e.getPhone()>-1){
                registered = true;
            }
        }

        if(registered==false){
            updateLocal(refreshedToken,e);
        }else{
            updateLocal(refreshedToken,e);
            updateServer(list.get(0).getPhone(),refreshedToken);
        }
        datasource.close();
    }
    private void updateLocal(String newToken,User e){
        if(e==null){
            datasource.createElderly(newToken);
        }else{
            datasource.updateElderly(newToken,e.getFirebaseToken());
        }
    }
    private void updateServer(int phoneNumber,String refreshedToken){
        Log.d(TAG, "updateServer");
        new Thread(new Task(phoneNumber,refreshedToken)).start();
    }
    private class Task implements Runnable{
        private int phoneNumber;
        private String refreshedToken;
        public Task(int phoneNumber,String refreshedToken){
            this.phoneNumber = phoneNumber;
            this.refreshedToken = refreshedToken;
        }
        @Override
        public void run() {
            String url_text = getResources().getString(R.string.gae_url);
            String action = getResources().getString(R.string.gae_addUser_url);
            URL url;
            HttpURLConnection urlConnection = null;
            String resp = null;
            try {
                url = new URL(url_text+action);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                JSONObject inputJO = new JSONObject();
                inputJO.put("token",refreshedToken);
                inputJO.put("phoneNumber",phoneNumber);

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                String parameters = "data="+inputJO.toString();
                wr.write(parameters.getBytes());

                InputStream in = urlConnection.getInputStream();
                InputStreamReader isw = new InputStreamReader(in);
                int data = isw.read();
                StringBuilder sb = new StringBuilder();
                while (data != -1) {
                    char current = (char) data;
                    data = isw.read();
                    sb.append(current);
                }
                resp = sb.toString();
                Log.d(TAG, "updateServer OK");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }
}
