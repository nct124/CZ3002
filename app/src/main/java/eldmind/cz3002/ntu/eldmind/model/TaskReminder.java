package eldmind.cz3002.ntu.eldmind.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by n on 11/2/2017.
 */

public class TaskReminder {
    public TaskReminder() {

    }
    public TaskReminder(int id, String title, String desc, String recurring,
                        Calendar dueTime,
                        String weeklyDay,String weeklyTime,
                        String status) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.recurring = recurring;
        this.dueTime = dueTime;
        this.weeklyDay = weeklyDay;
        this.weeklyTime = weeklyTime;
        this.status = status;
    }

    private int id;
    private String title;
    private String desc;
    private String recurring; //single, daily, weekly
    private Calendar dueTime; //for SINGLE
    private String weeklyDay; //for WEEKLY
    private String weeklyTime; //for WEEKLY
    private String status; //ENABLED OR DISABLED

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRecurring() {
        return recurring;
    }

    public void setRecurring(String recurring) {
        this.recurring = recurring;
    }

    public Calendar getDueTime() {
        return dueTime;
    }

    public void setDueTime(Calendar dueTime) {
        this.dueTime = dueTime;
    }

    public String getWeeklyDay() {
        return weeklyDay;
    }

    public void setWeeklyDay(String weeklyDay) {
        this.weeklyDay = weeklyDay;
    }

    public String getWeeklyTime() {
        return weeklyTime;
    }

    public void setWeeklyTime(String weeklyTime) {
        this.weeklyTime = weeklyTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString(){
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String date = dateformat.format(dueTime.getTime());
        return title+" \n " + date;
    }
}

