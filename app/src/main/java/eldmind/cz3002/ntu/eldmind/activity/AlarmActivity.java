package eldmind.cz3002.ntu.eldmind.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import eldmind.cz3002.ntu.eldmind.AsyncTask.DeleteReminderTask;
import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.SQL.TaskReminderDataSource;
import eldmind.cz3002.ntu.eldmind.SQL.UserDataSource;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;
import eldmind.cz3002.ntu.eldmind.model.User;
import eldmind.cz3002.ntu.eldmind.others.AlarmTask;

public class AlarmActivity extends AppCompatActivity {
    Context mContext;
    final String TAG = "AlarmActivity";
    Ringtone r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("alarm.secondActivity","alarm~~");
        mContext = this;
        setContentView(R.layout.activity_alarm);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Alarm");
        myToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(myToolbar);
        Button stopbutton = (Button)findViewById(R.id.stopAlarmButton);
        stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.stop();
                finish();
            }
        });

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();


        Intent i = getIntent();

        Log.d(TAG, "AlarmActivity id ==>" + i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_ID)+"\n"
                +i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE)+"\n"
                +i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC)+"\n"
                +i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING)+"\n"
                +i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY)+"\n"
                +i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME)+"\n"
                +i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS)+"\n");

        noti(this);

        if(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING).equals("SINGLE")) {
            UserDataSource datasourceU = new UserDataSource(mContext);
            datasourceU.open();
            List<User> list = datasourceU.getAllUser();
            datasourceU.close();

            String id = i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_ID);
            TaskReminderDataSource datasource = new TaskReminderDataSource(this);
            datasource.open();
            datasource.deleteTask(id);
            datasource.close();
            Toast.makeText(this,"DELETED ID:"+id,Toast.LENGTH_LONG).show();
            DeleteReminderTask task = new DeleteReminderTask(this,list.get(0).getPhone(),0,true);
            task.execute(Integer.parseInt(id));
        }else{//weekly
            TaskReminder tr = new TaskReminder();
            tr.setId(Integer.parseInt(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_ID)));
            tr.setTitle(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE));
            tr.setDesc(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC));
            tr.setRecurring(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING));
            tr.setWeeklyDay(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY));
            tr.setWeeklyTime(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME));
            tr.setStatus(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS));
            Calendar c = Calendar.getInstance();
            if(i.getLongExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME,0)!=0){
                c.setTimeInMillis(i.getLongExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME,0));
            }
            tr.setDueTime(c);
            new AlarmTask(this, tr,true).run();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("alarm.secondActivity","destroyed");
        r.stop();
    }

    public void noti(Context c){
        String title = getIntent().getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE);
        String desc = getIntent().getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC);
        Log.d(TAG, "");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Task Due: " + title)
                        .setContentText(desc);
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }
}
