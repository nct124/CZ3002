package eldmind.cz3002.ntu.eldmind.AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import eldmind.cz3002.ntu.eldmind.activity.ListTaskReminderActivity;

/**
 * Created by n on 26/2/2017.
 */

public class RegisterUserTask extends AsyncTask<String, Integer, String> {
    private static ProgressDialog dialog;
    private Activity mActivity;

    public RegisterUserTask(Activity mActivity) {
        this.mActivity = mActivity;
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

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        try {
            JSONObject resp = new JSONObject(s);
            if(resp.getBoolean("success")){
                mActivity.finish();
                Intent intent = new Intent(mActivity, ListTaskReminderActivity.class);
                mActivity.startActivity(intent);
                Toast.makeText(mActivity,"Registered successfully",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(mActivity,"Registered unsuccessfully",Toast.LENGTH_LONG).show();
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
    protected String doInBackground(String... params) {
        String url_text = mActivity.getResources().getString(R.string.gae_url);
        String action = mActivity.getResources().getString(R.string.gae_addUser_url);
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
            inputJO.put("token",params[0]);
            inputJO.put("phoneNumber",Long.parseLong(params[1]));

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
