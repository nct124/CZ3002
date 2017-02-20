package eldmind.cz3002.ntu.eldmind.others;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import eldmind.cz3002.ntu.eldmind.model.TaskReminder;
import eldmind.cz3002.ntu.eldmind.service.ScheduleService;

/**
 * Created by n on 3/2/2017.
 */

public class ScheduleClient {
    private Context mContext;
    private ScheduleService mBoundService;
    private boolean isBounded = false;
    private ServiceConnection mConnection;

    //==DO NOT TOUCH
    /**
     * Call this to connect your activity to your service
     */
    public void bindService(){
        Intent i = new Intent(mContext,ScheduleService.class);
        mContext.bindService(i,mConnection,Context.BIND_AUTO_CREATE);
        isBounded = true;
    }
    public ScheduleClient(Context c){
        mContext = c;
        /**
         * When you attempt to connect to the service, this connection will be called with the result.
         * If we have successfully connected we instantiate our service object so that we can call methods on it.
         */
        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with our service has been established,
                // giving us the service object we can use to interact with our service.

                mBoundService = ((ScheduleService.ServiceBinder) service).getService();
                Log.d("ALARM","mBoundService created");
            }

            public void onServiceDisconnected(ComponentName className) {
                mBoundService = null;
                Log.d("ALARM","mBoundService removed");
            }
        };
    }

    /**
     * When you have finished with the service call this method to stop it
     * releasing your connection and resources
     */
    public void doUnbindService() {
        if (isBounded) {
            // Detach our existing connection.
            mContext.unbindService(mConnection);
            isBounded = false;
        }
    }
    //==DO NOT TOUCH

    public void setAlarmForNotification(TaskReminder tr){
        mBoundService.setAlarm(tr);
    }
}
