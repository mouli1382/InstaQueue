package in.mobifirst.tagtree.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import in.mobifirst.tagtree.tokens.LandingActivity;

public class NotificationUtil {

    public static final int ACTIVITY_REQUEST_CODE = 1982;
    public static final int NOTIFICATION_ID = 1986;

    public static void sendNotification(Context context, String title, String message, Bundle bundle) {
        Intent intent = new Intent(context, LandingActivity.class);
        if (bundle != null) {
            intent.putExtra(ApplicationConstants.BUNDLE_KEY, bundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ACTIVITY_REQUEST_CODE, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_notification_overlay)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = notificationBuilder.build();
        notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
