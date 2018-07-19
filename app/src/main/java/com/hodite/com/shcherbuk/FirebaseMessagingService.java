package com.hodite.com.shcherbuk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.messaging.RemoteMessage;
import com.hodite.com.shcherbuk.WebActivity.WebActivity;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService implements Constants {

    SharedPreferences sp;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        sp = getSharedPreferences(CHECK_SETTINGS,
                Context.MODE_PRIVATE);

        if (remoteMessage.getData() == null) {
            sendNotification(remoteMessage.getNotification().getBody(), URL_HODITE_COM);
        }

        if (remoteMessage.getData() != null) {
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getData().get("URL"));
        }
    }

    private void sendNotification(final String body, final String url) {
        final Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(KEY_INTENT, url);
        final PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        final Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis()) //Время уведомления
                .setSmallIcon(R.mipmap.ico)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(body); // Текст уведомления

        final Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND |
                Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify((body + url).hashCode(), notification);
    }
}
