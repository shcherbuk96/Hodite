package com.hodite.com.shcherbuk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Администратор on 01.02.2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService implements Constants{

    SharedPreferences sp;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sp = getSharedPreferences(CHECK_SETTINGS,
                Context.MODE_PRIVATE);

        /*boolean webSite = sp.getBoolean(notifWebSite, true);
        boolean shops = sp.getBoolean(notifShops, true);
        if(webSite && shops){
            if(remoteMessage.getData().equals("WEB")){
                sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getData().get("WEB"));
            }
            if (remoteMessage.getData().equals("SHOP")){
                sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getData().get("SHOP"));
            }
        }
        else if(!webSite && !shops){

        }
        else if(!webSite && shops){
            if(remoteMessage.getData().equals("SHOP")){
                sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getData().get("WEB"));
            }
        }
        else if(webSite && !shops){
            if(remoteMessage.getData().equals("WEB")){
                sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getData().get("WEB"));
            }
        }*/



        if(remoteMessage.getData()==null){
            sendNotification(remoteMessage.getNotification().getBody(),URL_HODITE_COM);
        }

        if(remoteMessage.getData()!=null){
            sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getData().get("URL"));
        }

/*        else if(remoteMessage.getData().containsKey("SHOP")){
            sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getData().get("SHOP"));
        }
        else if(remoteMessage.getData().containsKey("WEB")){
            sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getData().get("WEB"));
        }*/

    }

    private void sendNotification(String body,String url) {
        Intent intent=new Intent(this,WebActivity.class);
        intent.putExtra(KEY_INTENT,url);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
// оставим только самое необходимое
        builder.setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis()) //Время уведомления
                .setSmallIcon(R.mipmap.ico)
                .setContentTitle("Hodite")
                .setContentText(body); // Текст уведомления

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND |
                Notification.DEFAULT_VIBRATE;
// ставим флаг, чтобы уведомление пропало после нажатия
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify((body+url).hashCode(),notification);
    }


}
