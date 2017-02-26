package eldmind.cz3002.ntu.eldmind.service;

/**
 * Created by n on 22/2/2017.
 */

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import eldmind.cz3002.ntu.eldmind.R;

public class FirebaseMessageService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Type: " + remoteMessage.getMessageType());
        Log.d(TAG, "Notification Message info: " + remoteMessage.getData().get("message"));
        //Do something
        noti(this,"ELDMIND",remoteMessage.getData().get("message"));
    }

    public void noti(Context c,String title,String message){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
}
