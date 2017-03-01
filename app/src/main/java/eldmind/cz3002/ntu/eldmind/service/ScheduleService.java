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
    public void createTaskAndAlarm(TaskReminder tr) {
        // This starts a new thread to set the alarm
        // You want to push off your tasks onto a new thread to free up the UI to carry on responding
        TaskReminderDataSource datasource = new TaskReminderDataSource(this);
        datasource.open();
        datasource.createNewTask(tr);
        datasource.close();
        new AlarmTask(this, tr).run();
    }
    //==DO NOT TOUCH

    public void updateTask(TaskReminder tr, String id) {
        TaskReminderDataSource datasource = new TaskReminderDataSource(this);
        datasource.open();
        datasource.updateTask(tr, id);
        datasource.close();

    }

    public void deleteTaskAndAlarm(String id) {
        TaskReminderDataSource ds = new TaskReminderDataSource(this);
        ds.open();
        ds.deleteTask(id);
        ds.close();

    }

    public class ServiceBinder extends Binder {
        public ScheduleService getService() {
            return ScheduleService.this;
        }
    }
}

