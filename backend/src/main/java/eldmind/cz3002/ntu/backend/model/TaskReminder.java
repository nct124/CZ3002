package eldmind.cz3002.ntu.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by n on 7/3/2017.
 */
@Entity
public class TaskReminder {
    @Id
    private String Id; //format: "phoneNum *IdGeneratedInAndroid*"
    private long phoneNum; //FK of User
    private String title;
    private String desc;
    private String reccurring;// SINGLE OR WEEKLY
    private long dueTime; //FOR SINGLE
    private String weeklyDay; //FOR WEEKLY eg "MONDAY,SATURDAY,SUNDAY"
    private String weeklyTime; //FOR WEEKLY eg "23:00"
    private String status; // ENABLED,DISABLED

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public long getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(long phoneNum) {
        this.phoneNum = phoneNum;
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

    public String getReccurring() {
        return reccurring;
    }

    public void setReccurring(String reccurring) {
        this.reccurring = reccurring;
    }

    public long getDueTime() {
        return dueTime;
    }

    public void setDueTime(long dueTime) {
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
}
