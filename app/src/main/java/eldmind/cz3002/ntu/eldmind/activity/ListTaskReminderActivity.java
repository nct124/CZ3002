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

import java.util.List;

import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.TaskReminderDataSource;
import eldmind.cz3002.ntu.eldmind.model.TaskReminder;

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
                intent.putExtra("id", tr.getId() + "");
                intent.putExtra("title",tr.getTitle());
                intent.putExtra("desc",tr.getDesc());
                intent.putExtra("dueTime",tr.getDueTime().getTimeInMillis());
                intent.putExtra("recurring",tr.getRecurring());
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
                Intent intent = new Intent(this, ManageTaskReminderActivity.class);
                intent.setAction("create");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
