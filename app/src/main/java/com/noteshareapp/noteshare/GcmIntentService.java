package com.noteshareapp.noteshare;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Random;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);


        Log.e("jay", "in gcm intent message " + messageType);
        Log.e("jay", "in gcm intent message bundle " + extras);


        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());

                Log.e("jay 1", "Send error: " + extras.toString());

            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString());

                Log.e("jay 2", "Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                String recieved_message=intent.getStringExtra("text_message");
                Log.e("jay title ", extras.getString("gcm.notification.title"));
                Log.e("jay body ", extras.getString("gcm.notification.body"));

                String title = extras.getString("gcm.notification.title");
                String body = extras.getString("gcm.notification.body");

                sendNotification(title, body);

                Log.e("jay 3", "message recieved :" +recieved_message);

                Intent sendIntent =new Intent("message_recieved");
                sendIntent.putExtra("message",recieved_message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }



    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title, String body) {

        /*Random random = new Random();
        int mid = random.nextInt(9999 - 1000) + 1000;

        Intent notificationIntent = new Intent(this, NotificationCenterActivity.class);
        PendingIntent intent = PendingIntent.getActivity(this, mid, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder notification = new Notification.Builder(this)
                .setColor(16734816)
                .setContentTitle(title)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(body))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                        //.setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher);
                //.setTicker(message);.setSmallIcon(R.drawable.ic_launcher)


        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notification.setContentIntent(intent);

        mNotificationManager.notify(mid, notification.build());*/


        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(this, NotificationCenterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        builder.setContentTitle(title);
        builder.setContentText(body);
        //builder.setSubText("Tap on notification for more details.");
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(10, builder.build());*/

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(GcmIntentService.this, NotificationCenterActivity.class);

        //Intent intent = new Intent(currentActivity.this, NotificationActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(NotificationCenterActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(10000);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setColor(16734816)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        //.setDefaults(Notification.DEFAULT_SOUND)
                        .setContentText(body);

        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(randomNumber, mBuilder.build());

    }
}
