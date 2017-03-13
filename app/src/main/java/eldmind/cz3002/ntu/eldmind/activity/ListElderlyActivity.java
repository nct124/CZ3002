package eldmind.cz3002.ntu.eldmind.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.ElderlyDataSource;
import eldmind.cz3002.ntu.eldmind.model.Elderly;

public class ListElderlyActivity extends AppCompatActivity {
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_elderly);
        mContext = this;
        final ListView listview = (ListView)findViewById(R.id.ListELD);
        listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Elderly e = (Elderly) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, ListTaskReminderForCustodianActivity.class);
                intent.putExtra("elderly_phoneNumber",e.getPhoneNumber());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ListView listview = (ListView)findViewById(R.id.ListELD);

        ElderlyDataSource datasource = new ElderlyDataSource(this);
        datasource.open();
        List<Elderly> list =  datasource.getAllElderly();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        datasource.close();
    }
}
