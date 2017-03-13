package eldmind.cz3002.ntu.eldmind.AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;

/**
 * Created by n on 11/3/2017.
 */

public class DeleteReminderTask extends AsyncTask<Integer, Integer, String> {
    private static ProgressDialog dialog;
    private Activity mActivity;
    private long elderlyPhoneNumber;
    private long custodianPhoneNumber;
    boolean alarm;
    public DeleteReminderTask(Activity mActivity,long elderlyPhoneNumber,long custodianPhoneNumber,boolean alarm) {
        this.mActivity = mActivity;
        this.elderlyPhoneNumber = elderlyPhoneNumber;
        this.custodianPhoneNumber = custodianPhoneNumber;
        this.alarm = alarm;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mActivity);
        dialog.setMessage("Please Wait");
        dialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(mActivity.isDestroyed()==false){
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        try {
            JSONObject resp = new JSONObject(s);
            if(resp.getBoolean("success")){
                if(!alarm){
                    Toast.makeText(mActivity,"Delete Task successfully",Toast.LENGTH_LONG).show();
                    mActivity.finish();
                }
            }else{
                Toast.makeText(mActivity,"Delete Task unsuccessfully, "+resp.getString("msg"),Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(Integer ... params) {
        String url_text = mActivity.getResources().getString(R.string.gae_url);
        String action = mActivity.getResources().getString(R.string.gae_updateTask_url);
        URL url;
        HttpURLConnection urlConnection = null;
        String resp = null;
        try {
            url = new URL(url_text+action);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            JSONObject inputJO = new JSONObject();
            inputJO.put("phoneNumber",elderlyPhoneNumber);
            inputJO.put("CphoneNumber",custodianPhoneNumber);
            inputJO.put(EldmindSQLiteHelper.COLUMN_TaskReminder_ID,params[0]);
            inputJO.put(EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS,"DISABLED");

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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return resp;
    }
}
