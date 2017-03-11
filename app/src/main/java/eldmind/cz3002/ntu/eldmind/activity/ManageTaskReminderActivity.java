package eldmind.cz3002.ntu.eldmind.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import eldmind.cz3002.ntu.eldmind.AsyncTask.AddReminderTask;
import eldmind.cz3002.ntu.eldmind.AsyncTask.DeleteReminderTask;
import eldmind.cz3002.ntu.eldmind.AsyncTask.UpdateReminderTask;
import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.SQL.UserDataSource;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;
import eldmind.cz3002.ntu.eldmind.model.User;
import eldmind.cz3002.ntu.eldmind.others.ScheduleClient;

import static eldmind.cz3002.ntu.eldmind.R.id.weekly;

public class ManageTaskReminderActivity extends AppCompatActivity {
    Activity a;
    Context mContext;
    ScheduleClient sc;
    Calendar c1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_task_reminder);
        mContext = this;
        a = this;
        sc = new ScheduleClient(mContext);
        sc.bindService();
        c1 = Calendar.getInstance();
        c1.set(Calendar.SECOND,0);

        final TextView timeBox = (TextView)findViewById(R.id.timePicker);
        final TextView dateBox = (TextView)findViewById(R.id.datePicker);
        Button dateSetter = (Button)findViewById(R.id.dateSetter);
        Button timeSetter = (Button)findViewById(R.id.timeSetter);
        Button setButton = (Button) findViewById(R.id.setButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        final EditText titleBox = (EditText)findViewById(R.id.titlebox);
        final EditText descBox = (EditText)findViewById(R.id.desc);
        final android.support.v7.widget.AppCompatSpinner recurringBox = (android.support.v7.widget.AppCompatSpinner)findViewById(R.id.Recurringbox);
        final String TAG = "ManageTaskReminder";


        if(getIntent()!=null){
            Intent intent = getIntent();
            if(intent.getAction().equals("edit")){ //Set old values back to form
                titleBox.setText(intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE));
                descBox.setText(intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC));
                if(intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING).equals("SINGLE")){
                    recurringBox.setSelection(0);
                    c1.setTimeInMillis(intent.getLongExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME,c1.getTimeInMillis()));
                    SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
                    String time = timeformat.format(c1.getTime());
                    timeBox.setText(time);
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
                    String date = dateformat.format(c1.getTime());
                    dateBox.setText(date);
                }else if(intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING).equals("WEEKLY")){
                    recurringBox.setSelection(1);
                    setDaysSelected(intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY));

                    String weeklyTime = intent.getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME);
                    int hours = Integer.parseInt(weeklyTime.split(":")[0]);
                    int mins = Integer.parseInt(weeklyTime.split(":")[1]);
                    c1.set(Calendar.HOUR_OF_DAY,hours);
                    c1.set(Calendar.MINUTE,mins);

                    SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
                    String time = timeformat.format(c1.getTime());
                    timeBox.setText(time);
                }
            }else if(intent.getAction().equals("create")){
                deleteButton.setVisibility(View.GONE);
                setButton.setText("Create");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1;
                setButton.setLayoutParams(lp);

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                String date = format.format(c1.getTime());
                dateBox.setText(date);
                SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                String time = format2.format(c1.getTime());
                timeBox.setText(time);
            }
        }
        recurringBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://SINGLE
                        findViewById(weekly).setVisibility(View.GONE);
                        findViewById(R.id.single).setVisibility(View.VISIBLE);
                        break;
                    case 1://WEEKLY
                        findViewById(R.id.single).setVisibility(View.GONE);
                        findViewById(weekly).setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        dateSetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog date = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        c1.set(Calendar.YEAR,year);
                        c1.set(Calendar.MONTH,month);
                        c1.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String date = format.format(c1.getTime());
                        dateBox.setText(date);
                    }
                },c1.get(Calendar.YEAR),c1.get(Calendar.MONTH),c1.get(Calendar.DAY_OF_MONTH));
                date.show();
            }
        });
        timeSetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog time = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        c1.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        c1.set(Calendar.MINUTE,minute);
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                        String time = format.format(c1.getTime());
                        timeBox.setText(time);
                    }
                },c1.get(Calendar.HOUR_OF_DAY),c1.get(Calendar.MINUTE),true);
                time.show();
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar curr = Calendar.getInstance();
                if(recurringBox.getSelectedItem().toString().toUpperCase().equals("SINGLE") && curr.after(c1)){
                    Toast.makeText(mContext,"Invalid Time, it's already over",Toast.LENGTH_LONG).show();
                }else if(recurringBox.getSelectedItem().toString().toUpperCase().equals("WEEKLY")&&checkDaysSelected().equals("")){
                    Toast.makeText(mContext,"Select at least 1 day",Toast.LENGTH_LONG).show();
                }else if(titleBox.getText().toString().equals("")) {
                    Toast.makeText(mContext,"Enter title",Toast.LENGTH_LONG).show();
                }else if(descBox.getText().toString().equals("")) {
                    Toast.makeText(mContext,"Enter description",Toast.LENGTH_LONG).show();
                }else{
                    UserDataSource datasource = new UserDataSource(mContext);
                    datasource.open();
                    List<User> list = datasource.getAllUser();
                    datasource.close();

                    TaskReminder tr = new TaskReminder();
                    tr.setTitle(titleBox.getText().toString());
                    tr.setDesc(descBox.getText().toString());
                    tr.setRecurring(recurringBox.getSelectedItem().toString().toUpperCase());
                    if(tr.getRecurring().equals("SINGLE")){
                        tr.setDueTime(c1);
                        tr.setWeeklyDay(null);
                        tr.setWeeklyTime(null);
                    }else{ //weekly
                        tr.setDueTime(null);
                        String weeklyDay = checkDaysSelected();
                        tr.setWeeklyDay(weeklyDay);//add something
                        String weeklyTime = c1.get(Calendar.HOUR_OF_DAY)+":"+c1.get(Calendar.MINUTE);
                        tr.setWeeklyTime(weeklyTime);//add something
                    }
                    tr.setStatus("ENABLED");
                    if (getIntent().getAction().equals("edit")) { //Update Task
                        String oldId = getIntent().getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_ID);
                        tr.setId(Integer.parseInt(oldId));
                        Log.d(TAG, "onClick: updateTask id == " + oldId);
                        sc.updateTask(tr);
                        UpdateReminderTask task = new UpdateReminderTask(a,list.get(0).getPhone());
                        task.execute(tr);
                    } else { //Create New Task
                        int id = sc.createTask(tr);
                        AddReminderTask task = new AddReminderTask(a,list.get(0).getPhone());
                        task.execute(tr);
                        Log.d(TAG, "onClick: createTask id == " + id);
                    }
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getAction().equals("edit")){
                    UserDataSource datasource = new UserDataSource(mContext);
                    datasource.open();
                    List<User> list = datasource.getAllUser();
                    datasource.close();

                    //Only if it is updating previous value then delete otherwise do nothing.
                    String oldId = getIntent().getStringExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_ID);
                    sc.deleteTask(oldId); //Delete Task and Alarm
                    Integer oldIdIn = new Integer(oldId);
                    DeleteReminderTask task = new DeleteReminderTask(a,list.get(0).getPhone());
                    task.execute(oldIdIn);
                }else {
                    Toast.makeText(mContext, "Task Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void setDaysSelected(String weeklyDays) {
        String[]days = weeklyDays.split(",");
        for(int i = 0;i<days.length;i++){
            if(days[i].equals("MON")){
                ((ToggleButton)findViewById(R.id.weekly_monday)).setChecked(true);
            }else if(days[i].equals("TUES")){
                ((ToggleButton)findViewById(R.id.weekly_tuesday)).setChecked(true);
            }else if(days[i].equals("WED")){
                ((ToggleButton)findViewById(R.id.weekly_wednesday)).setChecked(true);
            }else if(days[i].equals("THU")){
                ((ToggleButton)findViewById(R.id.weekly_thursday)).setChecked(true);
            }else if(days[i].equals("FRI")){
                ((ToggleButton)findViewById(R.id.weekly_friday)).setChecked(true);
            }else if(days[i].equals("SAT")){
                ((ToggleButton)findViewById(R.id.weekly_saturday)).setChecked(true);
            }else if(days[i].equals("SUN")){
                ((ToggleButton)findViewById(R.id.weekly_sunday)).setChecked(true);
            }
        }
    }
    private String checkDaysSelected(){
        String days = "";
        boolean first = true;
        if(((ToggleButton)findViewById(R.id.weekly_monday)).isChecked()){
            if(first){
                days += "MON";
                first = false;
            }else {
                days += ",MON";
            }
        }
        if(((ToggleButton)findViewById(R.id.weekly_tuesday)).isChecked()){
            if(first){
                days += "TUES";
                first = false;
            }else {
                days += ",TUES";
            }
        }
        if(((ToggleButton)findViewById(R.id.weekly_wednesday)).isChecked()){
            if(first){
                days += "WED";
                first = false;
            }else {
                days += ",WED";
            }
        }
        if(((ToggleButton)findViewById(R.id.weekly_thursday)).isChecked()){
            if(first){
                days += "THU";
                first = false;
            }else {
                days += ",THU";
            }
        }
        if(((ToggleButton)findViewById(R.id.weekly_friday)).isChecked()){
            if(first){
                days += "FRI";
                first = false;
            }else {
                days += ",FRI";
            }
        }
        if(((ToggleButton)findViewById(R.id.weekly_saturday)).isChecked()){
            if(first){
                days += "SAT";
                first = false;
            }else {
                days += ",SAT";
            }
        }
        if(((ToggleButton)findViewById(R.id.weekly_sunday)).isChecked()){
            if(first){
                days += "SUN";
                first = false;
            }else {
                days += ",SUN";
            }
        }
        return days;
    }
    protected void onStop(){
        if(sc!=null){
            sc.doUnbindService();
        }
        super.onStop();
    }
}
