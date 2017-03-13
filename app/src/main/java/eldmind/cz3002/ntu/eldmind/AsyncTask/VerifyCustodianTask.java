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
import eldmind.cz3002.ntu.eldmind.SQL.ElderlyDataSource;

/**
 * Created by n on 11/3/2017.
 */

public class VerifyCustodianTask extends AsyncTask<Long, Integer, String> {
    private static ProgressDialog dialog;
    private Activity mActivity;
    private String verifyCode;

    public VerifyCustodianTask(Activity mActivity,String verifyCode) {
        this.mActivity = mActivity;
        this.verifyCode = verifyCode;
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
        if (mActivity.isDestroyed() == false) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        try {
            JSONObject resp = new JSONObject(s);
            if (resp.getBoolean("success")) {
                Toast.makeText(mActivity, resp.getString("msg"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mActivity, resp.getString("msg"), Toast.LENGTH_LONG).show();
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
    protected String doInBackground(Long... params) {
        String url_text = mActivity.getResources().getString(R.string.gae_url);
        String action = mActivity.getResources().getString(R.string.gae_verifyCustodian_url);
        URL url;
        HttpURLConnection urlConnection = null;
        String resp = null;
        try {
            url = new URL(url_text + action);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            JSONObject inputJO = new JSONObject();
            inputJO.put("caregiver_phoneNumber", params[0]);
            inputJO.put("elderly_phoneNumber", params[1]);
            inputJO.put("verify_code", verifyCode);
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            String parameters = "data=" + inputJO.toString();
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
            JSONObject respJO = new JSONObject(resp);
            if (respJO.getBoolean("success")) {
                ElderlyDataSource datasource = new ElderlyDataSource(mActivity);
                datasource.open();
                int phone = params[1].intValue();
                datasource.createElderly(phone);
                datasource.close();
            }
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
