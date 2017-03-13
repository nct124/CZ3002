package eldmind.cz3002.ntu.eldmind.service;

/**
 * Created by n on 22/2/2017.
 */

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.SQL.TaskReminderDataSource;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;
import eldmind.cz3002.ntu.eldmind.others.AlarmTask;

import static android.R.attr.id;

public class FirebaseMessageService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Type: " + remoteMessage.getMessageType());
        Log.d(TAG, "Notification Message info: " + remoteMessage.getData().get("message"));
        //Do something
        Map<String,String> data = remoteMessage.getData();
        String action = data.get("action");
        if(action.equals("verify")){
            noti(this,"Verification Code",data.get("message"));//done
        }else if(action.equals("create")){
            //convert to TR
            TaskReminder tr = toTask(data.get("task"));
            //save TR
            TaskReminderDataSource datasource = new TaskReminderDataSource(this);
            datasource.open();
            int id = datasource.createNewTask(tr);
            datasource.close();
            tr.setId(id);
            //set alarm
            new AlarmTask(this, tr,true).run();
            noti(this,"Task created by "+ data.get("by"),tr.toString());
        }else if(action.equals("update")){
            //convert to TR
            TaskReminder tr = toTask(data.get("task"));
            //save TR
            TaskReminderDataSource datasource = new TaskReminderDataSource(this);
            datasource.open();
            datasource.updateTask(tr,Integer.toString(tr.getId()));
            datasource.close();
            tr.setId(id);
            //set alarm
            new AlarmTask(this, tr,false).run();
            new AlarmTask(this, tr,true).run();
            noti(this,"Task updated by "+ data.get("by"),tr.toString());
        }else if(action.equals("delete")){
            //convert to TR
            TaskReminder tr = toTask(data.get("task"));
            //save TR
            TaskReminderDataSource datasource = new TaskReminderDataSource(this);
            datasource.open();
            datasource.deleteTask(Integer.toString(tr.getId()));
            datasource.close();
            tr.setId(id);
            //set alarm
            new AlarmTask(this, tr,false).run();
            noti(this,"Task deleted by "+ data.get("by"),tr.toString());
        }
    }
    public TaskReminder toTask(String joStr){
        try {
            JSONObject jo = new JSONObject(joStr);
            TaskReminder tr = new TaskReminder();
            tr.setId(jo.getInt(EldmindSQLiteHelper.COLUMN_TaskReminder_ID));
            tr.setTitle(jo.getString(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE));
            tr.setRecurring(jo.getString(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING));
            if(tr.getRecurring().equals("SINGLE")){
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(jo.getLong(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME));
                tr.setDueTime(c);
                tr.setWeeklyDay("");
                tr.setWeeklyTime("");
            }else{
                tr.setWeeklyDay(jo.getString(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY));
                tr.setWeeklyTime(jo.getString(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME));
            }
            tr.setStatus(jo.getString("status"));
            tr.setDesc(jo.getString("desc"));
            return tr;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void noti(Context c,String title,String message){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
}
