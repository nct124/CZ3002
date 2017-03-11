package eldmind.cz3002.ntu.eldmind.others;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.activity.AlarmActivity;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by n on 3/2/2017.
 */

public class AlarmTask implements Runnable {
    private Context mContext;
    private final String TAG = "AlarmTask";
    TaskReminder tr;
    AlarmManager am;
    boolean create;
    public AlarmTask(Context c,TaskReminder tr,boolean create){
        mContext = c;
        this.tr = tr;
        this.create = create;
        am = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
    }
    @Override
    public void run() {
        if(create==true){
            Log.d(TAG,"check tr:"+tr.toString());
            Intent intent = new Intent(mContext, AlarmActivity.class);
            intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_ID, Integer.toString( tr.getId() ) );
            intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE,tr.getTitle());
            intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC,tr.getDesc());
            intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING,tr.getRecurring());
            intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY,tr.getWeeklyDay());
            intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME,tr.getWeeklyTime());
            if(tr.getDueTime()!=null){
                intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME,tr.getDueTime().getTimeInMillis());
            }else{
                intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME,(long)0);
            }
            intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS,tr.getStatus());
            long mtime = 0;
            if(tr.getRecurring().equals("SINGLE")){
                mtime = tr.getDueTime().getTimeInMillis();
            }else{//WEEKLY
                mtime = findNextTiming(tr.getWeeklyDay(),tr.getWeeklyTime());
            }
            intent.setAction(Long.toString(tr.getId()));
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            /*Log.d(TAG, "AlarmTask.run id ==>" + intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_ID)+"\n"
                                                +intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE)+"\n"
                                                +intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC)+"\n"
                                                +intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING)+"\n"
                                                +intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY)+"\n"
                                                +intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME)+"\n"
                                                +intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS)+"\n");*/
            // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
            am.set(AlarmManager.RTC, mtime, pendingIntent);
        }else{
            Intent intent = new Intent(mContext, AlarmActivity.class);
            intent.setAction(Integer.toString(tr.getId()));
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.d(TAG, "remove alarm id ==>" + tr.getId());
            am.cancel(pendingIntent);
        }
    }
    private long findNextTiming(String weeklyDay,String weeklyTime){
        Calendar c = Calendar.getInstance();
        String timenow = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
        boolean today = true;
        if(timenow.equals(weeklyTime)){
            c.add(Calendar.DATE,1);
        }
        String [] days = weeklyDay.split(",");
        while(1==1){
            if(c.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY && find(days,"MON")){
                break;
            }
            if(c.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY && find(days,"TUES")){
                break;
            }
            if(c.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY && find(days,"WED")){
                break;
            }
            if(c.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY && find(days,"THU")){
                break;
            }
            if(c.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY && find(days,"FRI")){
                break;
            }
            if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY && find(days,"SAT")){
                break;
            }
            if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY && find(days,"SUN")){
                break;
            }
            c.add(Calendar.DATE,1);
            today=false;
        }
        //PROBLEM if the day happens to be today but the time is alrdy over
        if(today==true) {
            if(timenow.compareTo(weeklyTime)>0){
                c.add(Calendar.DATE,1);
                while(1==1){
                    if(c.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY && find(days,"MON")){
                        break;
                    }
                    if(c.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY && find(days,"TUES")){
                        break;
                    }
                    if(c.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY && find(days,"WED")){
                        break;
                    }
                    if(c.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY && find(days,"THU")){
                        break;
                    }
                    if(c.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY && find(days,"FRI")){
                        break;
                    }
                    if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY && find(days,"SAT")){
                        break;
                    }
                    if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY && find(days,"SUN")){
                        break;
                    }
                    c.add(Calendar.DATE,1);
                    today=false;
                }
            }
        }

        c.set(Calendar.HOUR_OF_DAY,Integer.parseInt(weeklyTime.split(":")[0]));
        c.set(Calendar.MINUTE,Integer.parseInt(weeklyTime.split(":")[1]));
        c.set(Calendar.SECOND,0);

        Log.d("AlarmTask","next alarm time: "+c.getTime().toString());
        return c.getTimeInMillis();
    }
    private boolean find(String []arr, String target){
        for(int i=0;i<arr.length;i++){
            if(arr[i].equals(target)){
                return true;
            }
        }
        return false;
    }
}
