package eldmind.cz3002.ntu.eldmind.broadcast_receiver;

/**
 * Created by n on 19/2/2017.
 */

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
            if(tr.getRecurring().equals("SINGLE")){
                if(tr.getDueTime().getTimeInMillis()>now.getTimeInMillis()){
                    Log.d("BOOT_RECEIVER","SINGLE: "+tr.getDueTime().getTime().toString());
                    AlarmTask at = new AlarmTask(context,tr,true);
                    at.run();
                }
            }else{//weekly
                Log.d("BOOT_RECEIVER","WEEKLY: "+tr.getWeeklyDay()+" "+tr.getWeeklyTime());
                new AlarmTask(context, tr,true).run();
            }
            /*if(tr.getDueTime().getTimeInMillis()>now.getTimeInMillis()){
                Toast.makeText(context, tr.getDueTime().getTime().toString()+">"+now.getTime().toString(), Toast.LENGTH_SHORT).show();
                Log.d("BOOTRECEIVER",tr.getDueTime().getTime().toString()+">"+now.getTime().toString());
                AlarmTask at = new AlarmTask(context,tr,true);
                at.run();
            }else{
                if(tr.getRecurring().equals("SINGLE")){
                    Toast.makeText(context, tr.getDueTime().getTime().toString()+"<"+now.getTime().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("BOOTRECEIVER",tr.getDueTime().getTime().toString()+"<"+now.getTime().toString());
                    //datasource.deleteTaskReminder(tr);
                    //Toast.makeText(context, "value of id ==> " + tr.getId(), Toast.LENGTH_SHORT).show();
                    datasource.deleteTask( Integer.toString(tr.getId()) );

                }else if(tr.getRecurring().equals("WEEKLY")){
                    tr.getDueTime().add(Calendar.DATE,7);
                }
            }*/
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
                        .setContentTitle("Alert Title todo")
                        .setContentText("Alert Text todo");
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }
}

