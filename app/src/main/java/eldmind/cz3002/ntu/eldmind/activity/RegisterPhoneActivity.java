package eldmind.cz3002.ntu.eldmind.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Register");
        myToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(myToolbar);
        mContext = this;
        final Button registerButton = (Button)findViewById(R.id.register_button);
        final EditText phoneBox = (EditText)findViewById(R.id.phonebox);

        //Check if previous number exist. If so, skip to next screen
        datasource = new ElderlyDataSource(mContext);
        datasource.open();
        List<Elderly> list = datasource.getAllElderly();
        datasource.close();
        if (list.size() > 0) {
            if (list.get(0).getPhone() != -1) //If phone number is -1 == logged out
            {
                Intent intent = new Intent(this, ListTaskReminderActivity.class);
                startActivity(intent);
                finish();
            }
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int phoneNumber;
                try {
                    phoneNumber = Integer.parseInt(phoneBox.getText().toString());
                } catch (Exception ex) {
                    Toast.makeText(RegisterPhoneActivity.this, "Please enter a phone number!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "registerButton: Error ==>" + ex.getMessage());
                    return; //Break after catch
                }

                datasource = new ElderlyDataSource(mContext);
                datasource.open();

                Elderly e = null;
                List<Elderly> list = datasource.getAllElderly();
                if (list.size() > 0) { //Get previous firebase token and the existing phonenumber
                    e = list.get(0);
                    updateLocal(e.getFirebaseToken(),phoneNumber);
                    updateServer(e.getFirebaseToken(),phoneNumber);
                    //finish(); //Prevents the user from pressing back
                }
                datasource.close();

            }
        });

    }
    private void updateLocal(String token,int phoneNumber){
        datasource.updateElderly(phoneNumber,token);
        //Toast.makeText(mContext,phoneNumber+" "+token,Toast.LENGTH_SHORT).show();
    }
    private void updateServer(String token,int phoneNumber){
        Log.d(TAG, "updateServer");
        String [] params = {token,Long.toString(phoneNumber)};
        RegisterElderlyTask task = new RegisterElderlyTask(this);
        task.execute(params);
    }
}
