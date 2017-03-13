package eldmind.cz3002.ntu.eldmind.AsyncTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.activity.ListTaskReminderForCustodianActivity;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;

/**
 * Created by n on 11/3/2017.
 */

public class getTaskReminderTask extends AsyncTask<Long, Integer, String> {
    private static ProgressDialog dialog;
    private ListTaskReminderForCustodianActivity mActivity;
    public getTaskReminderTask(ListTaskReminderForCustodianActivity mActivity) {
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
        if(mActivity.isDestroyed()==false) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        try {
            JSONObject resp = new JSONObject(s);
            JSONArray ja = resp.getJSONArray("reminders");
            List<TaskReminder> list = new ArrayList<TaskReminder>();
            for(int i=0;i<ja.length();i++){
                JSONObject jo = ja.getJSONObject(i);
                TaskReminder tr = new TaskReminder();
                tr.setId(jo.getInt(EldmindSQLiteHelper.COLUMN_TaskReminder_ID));
                if(jo.getLong(EldmindSQLiteHelper.COLUMN_TaskReminder_ID)!=0){
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(jo.getLong(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME));
                    tr.setDueTime(c);
                }
                tr.setStatus(jo.getString(EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS));
                tr.setDesc(jo.getString(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC));
                tr.setRecurring(jo.getString(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING));
                tr.setTitle(jo.getString(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE));
                tr.setWeeklyDay(jo.getString(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY));
                tr.setWeeklyTime(jo.getString(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME));
                list.add(tr);
            }
            mActivity.reminders = list;
            mActivity.displayReminders();
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
        String action = mActivity.getResources().getString(R.string.gae_getTask_url);
        URL url;
        HttpURLConnection urlConnection = null;
        String resp = null;
        try {
            JSONObject inputJO = new JSONObject();
            inputJO.put("elderly_phoneNumber", params[0]);
            String parameters = "data=" + inputJO.toString();

            url = new URL(url_text + action);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
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
