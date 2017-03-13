package eldmind.cz3002.ntu.eldmind.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import eldmind.cz3002.ntu.eldmind.AsyncTask.AddElderlyTask;
import eldmind.cz3002.ntu.eldmind.AsyncTask.VerifyCustodianTask;
import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.SQL.TaskReminderDataSource;
import eldmind.cz3002.ntu.eldmind.SQL.UserDataSource;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;
import eldmind.cz3002.ntu.eldmind.model.User;

public class ListTaskReminderActivity extends AppCompatActivity {
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task_reminder);
        mContext = this;
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Task Reminders");
        myToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(myToolbar);

        final ListView listview = (ListView)findViewById(R.id.ListTR);
        listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskReminder tr = (TaskReminder) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, ManageTaskReminderActivity.class);
                intent.setAction("edit");
                Toast.makeText(mContext,"ID: "+tr.getId(),Toast.LENGTH_LONG).show();
                intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_ID, Integer.toString(tr.getId()));
                intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE,tr.getTitle());
                intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC,tr.getDesc());
                if(tr.getDueTime()!=null){
                    intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME,tr.getDueTime().getTimeInMillis());
                }
                intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING,tr.getRecurring());
                intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY,tr.getWeeklyDay());
                intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME,tr.getWeeklyTime());
                intent.putExtra(EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS,tr.getStatus());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final ListView listview = (ListView)findViewById(R.id.ListTR);

        TaskReminderDataSource datasource = new TaskReminderDataSource(this);
        datasource.open();
        List<TaskReminder> list =  datasource.getAllTaskReminder();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        datasource.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_task_reminder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.create_task_reminder:
                Intent MTRAIntent = new Intent(this, ManageTaskReminderActivity.class);
                MTRAIntent.setAction("create");
                startActivity(MTRAIntent);
                return true;
            case R.id.logout:
                Toast.makeText(mContext, "Logout", Toast.LENGTH_SHORT).show();
                UserDataSource datasource = new UserDataSource(mContext);
                datasource.open();
                datasource.removePhoneNumber();
                datasource.close();
                Intent registerIntent = new Intent(this, RegisterPhoneActivity.class);
                startActivity(registerIntent);
                finish();
                return true;
            case R.id.refresh:
                Toast.makeText(mContext, "Refresh", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.addCustodee:
                askPhoneNumber();
                return true;
            case R.id.verify:
                verifyCode();
                return true;
            case R.id.check_elderly:
                Intent i = new Intent(this, ListElderlyActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void verifyCode() {
        UserDataSource datasource = new UserDataSource(mContext);
        datasource.open();
        final List<User> list = datasource.getAllUser();
        datasource.close();
        if (list.size() > 0) {
            if (list.get(0).getPhone() != -1) {//if registered
                LayoutInflater li = LayoutInflater.from(this);
                View dialogView = li.inflate(R.layout.verify_custodian_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setView(dialogView);
                final EditText userInput_v_code = (EditText) dialogView.findViewById(R.id.editTextDialogUserInput_verify_code);
                final EditText userInput_v_phone = (EditText) dialogView.findViewById(R.id.editTextDialogUserInput_verify_phoneNumber);
                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String verifyCode = userInput_v_code.getText().toString();
                        long phoneNumber_custodian = list.get(0).getPhone();
                        long phoneNumber_elderly = Long.parseLong(userInput_v_phone.getText().toString());
                        confirmCustodian(phoneNumber_custodian,phoneNumber_elderly,verifyCode);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }else{//not registered
                Toast.makeText(mContext,"You are not registered",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void confirmCustodian(long yourPhoneNumber,long elderlyPhoneNumber,String verifyCode) {
        VerifyCustodianTask task = new VerifyCustodianTask(this,verifyCode);
        task.execute(yourPhoneNumber,elderlyPhoneNumber);
    }

    private void askPhoneNumber() {
        UserDataSource datasource = new UserDataSource(mContext);
        datasource.open();
        final List<User> list = datasource.getAllUser();
        datasource.close();
        if (list.size() > 0) {
            if (list.get(0).getPhone() != -1) {//if registered
                LayoutInflater li = LayoutInflater.from(this);
                View dialogView = li.inflate(R.layout.ask_for_phone_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setView(dialogView);
                final EditText userInput = (EditText) dialogView.findViewById(R.id.editTextDialogUserInput);
                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        long phoneNumber = Long.parseLong(userInput.getText().toString());
                        addCustodian(list.get(0).getPhone(),phoneNumber);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }else{//not registered
                Toast.makeText(mContext,"You are not registered",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addCustodian(long yourPhoneNumber,long elderlyPhoneNumber) {
        AddElderlyTask task = new AddElderlyTask(this);
        task.execute(yourPhoneNumber,elderlyPhoneNumber);
    }
}
