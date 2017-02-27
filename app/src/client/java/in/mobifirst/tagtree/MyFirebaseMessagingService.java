package in.mobifirst.tagtree;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import in.mobifirst.tagtree.util.NotificationUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";

    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());

        Map<String, String> gcmMsg = remoteMessage.getData();
        Log.d(TAG, "FCM Data Message: " + gcmMsg);


        if (gcmMsg != null)
            NotificationUtil.sendNotification(MyFirebaseMessagingService.this,
                    "Firebase Push", gcmMsg.get("message"), null);
    }

}
