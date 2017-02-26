package eldmind.cz3002.ntu.eldmind.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import eldmind.cz3002.ntu.eldmind.AsyncTask.RegisterElderlyTask;
import eldmind.cz3002.ntu.eldmind.R;
import eldmind.cz3002.ntu.eldmind.SQL.ElderlyDataSource;
import eldmind.cz3002.ntu.eldmind.model.Elderly;

public class RegisterPhoneActivity extends AppCompatActivity {
    final String TAG = "RegisterPhoneActivity";
    ElderlyDataSource datasource;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        mContext = this;
        final Button registerButton = (Button)findViewById(R.id.register_button);
        final EditText phoneBox = (EditText)findViewById(R.id.phonebox);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasource = new ElderlyDataSource(mContext);
                datasource.open();
                int phoneNumber = Integer.parseInt(phoneBox.getText().toString());
                Elderly e = null;
                List<Elderly> list = datasource.getAllElderly();
                if(list.size()>0){
                    e = list.get(0);
                    updateLocal(e.getFirebaseToken(),phoneNumber);
                    updateServer(e.getFirebaseToken(),phoneNumber);
                }
                datasource.close();
            }
        });

    }
    private void updateLocal(String token,int phoneNumber){
        datasource.updateElderly(phoneNumber,token);
        Toast.makeText(mContext,phoneNumber+" "+token,Toast.LENGTH_LONG).show();
    }
    private void updateServer(String token,int phoneNumber){
        Log.d(TAG, "updateServer");
        String [] params = {token,Long.toString(phoneNumber)};
        RegisterElderlyTask task = new RegisterElderlyTask(this);
        task.execute(params);
    }
}
