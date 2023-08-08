package dev.bijon.pushnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService  extends FirebaseMessagingService {
    private static String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = "";
        String message = "";

        // Check if message contains a notification payload.

        if (remoteMessage.getNotification() != null) {
            try {
                title = remoteMessage.getNotification().getTitle();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                message = remoteMessage.getNotification().getBody();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Check if message contains a data payload :: when app in foreground
        Map<String, String> dataPayloads = remoteMessage.getData();
        if (dataPayloads.size() > 0) {
            for (String key : dataPayloads.keySet()) {
                String value = dataPayloads.get(key);
                Log.i(TAG, "Key: " + key + " Value: " + value);
                //do actions using data payloads
            }

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        sendNotification(title, message);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendTokenToServer(s);
    }

    //update your server token with new one
    private void sendTokenToServer(String newToken) {
        Log.i(TAG, "sendTokenToServer: " + newToken);
    }

    /**
     * Create and show a push notification when app is in foreground, otherwise (when in background) firebase automatically generates notification
     *
     * @param messageTitle FCM message title received.
     * @param messageBody  FCM message body received.
     */
    private void sendNotification(String messageTitle, String messageBody) {
        Log.i(TAG, "sendNotification: ");
        //'MainActivity' is the target activity. When notification will be clicked, 'MainActivity' will be triggered
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "OK";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification_red)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
