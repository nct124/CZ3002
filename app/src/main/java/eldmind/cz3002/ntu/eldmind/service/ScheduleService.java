package eldmind.cz3002.ntu.eldmind.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import eldmind.cz3002.ntu.eldmind.SQL.TaskReminderDataSource;
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

    public void createAlarm(TaskReminder tr) {
        new AlarmTask(this, tr,true).run();
    }
    public void deleteAlarm(TaskReminder tr) {
        new AlarmTask(this,tr,false).run();
    }
    public class ServiceBinder extends Binder {
        public ScheduleService getService() {
            return ScheduleService.this;
        }
    }
    //==DO NOT TOUCH

    public int createTask(TaskReminder tr) {
        TaskReminderDataSource datasource = new TaskReminderDataSource(this);
        datasource.open();
        int id = datasource.createNewTask(tr);
        datasource.close();
        tr.setId(id);
        createAlarm(tr);
        return id;
    }

    public void updateTask(TaskReminder tr) {
        TaskReminderDataSource datasource = new TaskReminderDataSource(this);
        datasource.open();
        datasource.updateTask(tr, Integer.toString(tr.getId()));
        datasource.close();
        deleteAlarm(tr);
        createAlarm(tr);
    }

    public void deleteTask(String id) {
        TaskReminder tr = new TaskReminder();
        tr.setId(Integer.parseInt(id));
        TaskReminderDataSource ds = new TaskReminderDataSource(this);
        ds.open();
        ds.deleteTask(id);
        ds.close();
        deleteAlarm(tr);
    }
}

