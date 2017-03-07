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

import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.SQL.TaskReminderDataSource;

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

        Log.d("alarm.secondActivity",i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE));
        Log.d("alarm.secondActivity",i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC));
        Log.d("alarm.secondActivity",i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING));
        Log.d("alarm.secondActivity",i.getAction());

        noti(this);

        if(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING).equals("SINGLE")) {
            //TODO After alarm sound, prompt if task finished instead of delete
            String id = i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_ID);
            //Toast.makeText(mContext, "My ID ==>" + id, Toast.LENGTH_SHORT).show();
            TaskReminderDataSource datasource = new TaskReminderDataSource(this);
            datasource.open();
            datasource.deleteTask(id);
            datasource.close();
        }else{

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
