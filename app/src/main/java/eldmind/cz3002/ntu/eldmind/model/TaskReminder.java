package eldmind.cz3002.ntu.eldmind.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by n on 11/2/2017.
 */

public class TaskReminder {
    public TaskReminder() {

    }
    public TaskReminder(int id, String title, String desc, Calendar dueTime, String recurring) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.dueTime = dueTime;
        this.recurring = recurring;
    }

    private int id;
    private String title;
    private String desc;
    private Calendar dueTime;
    private String recurring; //single, daily, weekly, monthly

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

    public Calendar getDueTime() {
        return dueTime;
    }

    public void setDueTime(Calendar dueTime) {
        this.dueTime = dueTime;
    }

    public String getRecurring() {
        return recurring;
    }

    public void setRecurring(String recurring) {
        this.recurring = recurring;
    }

    public String toString(){
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String date = dateformat.format(dueTime.getTime());
        return title+" \n " + date;
    }
}

