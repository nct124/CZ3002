package eldmind.cz3002.ntu.eldmind.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;
import eldmind.cz3002.ntu.eldmind.others.ScheduleClient;

public class ManageTaskReminderActivity extends AppCompatActivity {
    Context mContext;
    ScheduleClient sc;
    Calendar c1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_task_reminder);
        mContext = this;
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


        //TODO Allow updating of existing alarms, remember to delete the existing alarm
        if(getIntent()!=null){
            Intent intent = getIntent();
            if(intent.getAction().equals("edit")){

                titleBox.setText(intent.getStringExtra("title"));
                descBox.setText(intent.getStringExtra("desc"));
                c1.setTimeInMillis(intent.getLongExtra("dueTime",c1.getTimeInMillis()));
                SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
                String time = timeformat.format(c1.getTime());
                timeBox.setText(time);
                SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
                String date = dateformat.format(c1.getTime());
                dateBox.setText(date);

                if(intent.getStringExtra("recurring").equals("SINGLE")){
                    recurringBox.setSelection(0);
                }else if(intent.getStringExtra("recurring").equals("WEEKLY")){
                    recurringBox.setSelection(1);
                }


            }else if(intent.getAction().equals("create")){

            }
        }

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

                TaskReminder tr = new TaskReminder();
                tr.setTitle(titleBox.getText().toString());
                tr.setDesc(descBox.getText().toString());
                tr.setRecurring(recurringBox.getSelectedItem().toString().toUpperCase());
                tr.setDueTime(c1);

                if (getIntent().getAction().equals("edit")) {
                    String id = getIntent().getStringExtra("id");

                    //Create the old task reminder to delete/update alarm
                    TaskReminder otr = new TaskReminder();
                    otr.setTitle(getIntent().getStringExtra("title"));
                    otr.setDesc(getIntent().getStringExtra("desc"));
                    otr.setRecurring(getIntent().getStringExtra("recurring").toUpperCase());
                    long dueTime = getIntent().getLongExtra("dueTime", c1.getTimeInMillis());
                    Calendar oc = Calendar.getInstance();
                    oc.setTimeInMillis(dueTime);
                    otr.setDueTime(oc);

                    Toast.makeText(mContext, "myid =====>" + getIntent().getStringExtra("id"), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: updateTask id=" + id);
                    sc.updateTask(tr, otr, id);
                } else {
                    Log.d(TAG, "onClick: createTask");
                    sc.createTask(tr); //Also creates the new item
                    sc.createAlarm(tr);
                }

                String year = Integer.toString(c1.get(Calendar.YEAR));
                String month = Integer.toString(c1.get(Calendar.MONTH));
                String dayOfMonth = Integer.toString(c1.get(Calendar.DAY_OF_MONTH));
                String hourOfDay = Integer.toString(c1.get(Calendar.HOUR_OF_DAY));
                String minute = Integer.toString(c1.get(Calendar.MINUTE));
                String time = dayOfMonth+"-"+month+"-"+year+" "+hourOfDay+":"+minute;
                Toast.makeText(mContext,"alarm set: "+time,Toast.LENGTH_LONG).show();
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getIntent().getStringExtra("id");
                //Create the old task reminder to delete alarm
                TaskReminder otr = new TaskReminder();
                otr.setTitle(getIntent().getStringExtra("title"));
                otr.setDesc(getIntent().getStringExtra("desc"));
                otr.setRecurring(getIntent().getStringExtra("recurring").toUpperCase());
                long dueTime = getIntent().getLongExtra("dueTime", c1.getTimeInMillis());
                Calendar oc = Calendar.getInstance();
                oc.setTimeInMillis(dueTime);
                otr.setDueTime(oc);

                sc.deleteTaskAndAlarm(id, otr); //TODO Delete Alarm
                Toast.makeText(mContext, "Delete Button", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    protected void onStop(){
        if(sc!=null){
            sc.doUnbindService();
        }
        super.onStop();
    }
}
