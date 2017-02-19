package eldmind.cz3002.ntu.eldmind.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.SQL.TaskReminderDataSource;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;

public class AlarmActivity extends AppCompatActivity {
    Context mContext;
    Ringtone r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("alarm.secondActivity","alarm~~");
        mContext = this;
        setContentView(R.layout.activity_alarm);
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
            TaskReminder tr = new TaskReminder();
            tr.setTitle(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE));
            tr.setDesc(i.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC));

            TaskReminderDataSource datasource = new TaskReminderDataSource(this);
            datasource.open();
            datasource.deleteTaskReminder(tr);
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
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("REBOOT")
                        .setContentText("RESET ALARMS");
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }
}
