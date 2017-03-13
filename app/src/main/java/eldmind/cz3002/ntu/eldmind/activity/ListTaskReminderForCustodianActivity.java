package eldmind.cz3002.ntu.eldmind.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eldmind.cz3002.ntu.eldmind.AsyncTask.getTaskReminderTask;
import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.EldmindSQLiteHelper;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;

public class ListTaskReminderForCustodianActivity extends AppCompatActivity {
    Context mContext;
    int elderlyPhone;
    public List<TaskReminder> reminders;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task_reminder_for_custodian);
        mContext = this;
        elderlyPhone = getIntent().getIntExtra("elderly_phoneNumber",0);
        //TODO add a way to logout
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Task Reminders of "+elderlyPhone);
        myToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(myToolbar);

        final ListView listview = (ListView)findViewById(R.id.ListTRE);
        List<TaskReminder> reminders = new ArrayList<TaskReminder>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, reminders);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskReminder tr = (TaskReminder) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, ManageTaskReminderForCustodianActivity.class);
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
                intent.putExtra("elderly_phoneNumber",elderlyPhone);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_task_reminder_for_custodian_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getTaskReminderTask task = new getTaskReminderTask(this);
        task.execute((long)elderlyPhone);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.create_task_reminder:
                Intent MTRAIntent = new Intent(this, ManageTaskReminderForCustodianActivity.class);
                MTRAIntent.setAction("create");
                MTRAIntent.putExtra("elderly_phoneNumber",elderlyPhone);
                startActivity(MTRAIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void displayReminders(){
        final ListView listview = (ListView)findViewById(R.id.ListTRE);
        adapter.clear();
        adapter.addAll(reminders);
        adapter.notifyDataSetChanged();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
