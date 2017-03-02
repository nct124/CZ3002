package eldmind.cz3002.ntu.eldmind.broadcast_receiver;

/**
 * Created by n on 19/2/2017.
 */

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.List;

import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.TaskReminderDataSource;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;
import eldmind.cz3002.ntu.eldmind.others.AlarmTask;

/**
 * Created by n on 11/2/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        TaskReminderDataSource datasource = new TaskReminderDataSource(context);
        datasource.open();
        List<TaskReminder> list =  datasource.getAllTaskReminder();
        Calendar now = Calendar.getInstance();
        for(int i=0;i<list.size();i++){
            TaskReminder tr = list.get(i);
            if(tr.getDueTime().getTimeInMillis()>now.getTimeInMillis()){
                AlarmTask at = new AlarmTask(context,tr);
                at.run();
            }else{
                if(tr.getRecurring().equals("SINGLE")){
                    datasource.deleteTaskReminder(tr);
                }else if(tr.getRecurring().equals("WEEKLY")){
                    tr.getDueTime().add(Calendar.DATE,7);
                }
            }
        }
        datasource.close();
    }
    public void noti(Context c,TaskReminder tr){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setContentTitle(tr.getTitle())
                        .setContentText(tr.getDesc());
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify();
        mNotificationManager.notify(001, mBuilder.build());
    }
    public void noti(Context c){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Alert Title")
                        .setContentText("Alert Text");
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }
}

