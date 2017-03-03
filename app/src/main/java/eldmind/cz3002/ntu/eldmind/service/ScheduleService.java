package eldmind.cz3002.ntu.eldmind.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.SQL.TaskReminderDataSource;
import eldmind.cz3002.ntu.eldmind.activity.AlarmActivity;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;
import eldmind.cz3002.ntu.eldmind.others.AlarmTask;


/**
 * Created by n on 3/2/2017.
 */

public class ScheduleService extends Service {
    //==DO NOT TOUCH
    private final IBinder mBinder = new ServiceBinder();
    private final String TAG = "ScheduleService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;//super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * Show an alarm for a certain date when the alarm is called it will pop up a notification
     */
    public int createTask(TaskReminder tr) {
        // This starts a new thread to set the alarm
        // You want to push off your tasks onto a new thread to free up the UI to carry on responding
        TaskReminderDataSource datasource = new TaskReminderDataSource(this);
        datasource.open();
        int id = datasource.createNewTask(tr);
        datasource.close();

        if (id == -1){
            Log.d(TAG, "Error in CreateTask!");
        }else {
            Log.d(TAG, "Success in createTask");
        }

        return id;

    }

    public void createAlarm(TaskReminder tr) {
        new AlarmTask(this, tr).run();
        Log.d(TAG, "createAlarm id=" + tr.getId());
        //Toast.makeText(this, "create alarm id==>" + tr.getId(), Toast.LENGTH_SHORT).show();
    }
    //==DO NOT TOUCH

    public void updateTask(TaskReminder tr, TaskReminder otr, String id) {
        TaskReminderDataSource datasource = new TaskReminderDataSource(this);
        datasource.open();
        datasource.updateTask(tr, id);
        datasource.close();
        updateAlarm(tr, otr);
    }

    public void deleteAlarm(TaskReminder otr) {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_ID, otr.getId()+"");
        intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE, otr.getTitle());
        intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC, otr.getDesc());
        intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING, otr.getRecurring());
        intent.setAction(otr.getTitle() + " " + otr.getDueTime().getTimeInMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public void updateAlarm(TaskReminder tr, TaskReminder otr) {
        Toast.makeText(this, "updateAlarm id ==>" + otr.getId(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "updateAlarm otr.id = " + otr.getId() + ", tr.id =" + tr.getId());
        deleteAlarm(otr);
        createAlarm(tr);
    }

    public void deleteTaskAndAlarm(String id, TaskReminder otr) {
        TaskReminderDataSource ds = new TaskReminderDataSource(this);
        ds.open();
        ds.deleteTask(id);
        ds.close();
        deleteAlarm(otr);

    }

    public class ServiceBinder extends Binder {
        public ScheduleService getService() {
            return ScheduleService.this;
        }
    }
}

