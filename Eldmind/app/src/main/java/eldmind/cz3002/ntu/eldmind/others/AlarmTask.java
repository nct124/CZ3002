package eldmind.cz3002.ntu.eldmind.others;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.activity.AlarmActivity;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by n on 3/2/2017.
 */

public class AlarmTask implements Runnable {
    private Context mContext;
    TaskReminder tr;
    AlarmManager am;
    public AlarmTask(Context c,TaskReminder tr){
        mContext = c;
        this.tr = tr;
        am = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
    }
    @Override
    public void run() {
        Intent intent = new Intent(mContext, AlarmActivity.class);
        intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE,tr.getTitle());
        intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC,tr.getDesc());
        intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING,tr.getRecurring());
        intent.setAction(tr.getTitle()+" "+tr.getDueTime().getTimeInMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
        am.set(AlarmManager.RTC, tr.getDueTime().getTimeInMillis(), pendingIntent);
    }
}
