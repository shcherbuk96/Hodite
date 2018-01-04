package com.hodite.com.shcherbuk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyService extends Service implements Constants {

    NotificationManager nm;
    SharedPreferences sp;
    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(CHECK_SETTINGS,
                Context.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Timer myTimer = new Timer(); // Создаем таймер
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                push();
            }
        }, 600L*1000, 600L * 1000); // 600 миллисекунд до первого запуска,1800

        return START_STICKY;
    }


    public void push() {
        try {
            new asynchronousGet().run();
            Log.i("Good","good");
        } catch (Exception e) {
            Log.i("Eror","errorororor");
            e.printStackTrace();
        }
    }


    public void pushText(String str1,String str2){
        //String url="http://"+str2;

        Context context = getApplicationContext();

        Intent intent=new Intent(context,WebActivity.class);
        intent.putExtra(KEY_INTENT,str2);

        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
// оставим только самое необходимое
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ico)
                .setContentTitle("Hodite")
                .setContentText(str1); // Текст уведомления

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND |
                Notification.DEFAULT_VIBRATE;
        // ставим флаг, чтобы уведомление пропало после нажатия
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify((str1+str2).hashCode(),notification);
    }


    /*---------------------КЛАСС ДЛЯ СЧИТЫВАНИЕ ТЕКСТА ИЗ ФАЙЛА-----------*/
    public class asynchronousGet {
        private final OkHttpClient client = new OkHttpClient();

        public void run() throws Exception {
            Request request = new Request.Builder()
                    .url(URL_TEXT_TXT)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);

                        Headers responseHeaders = response.headers();
                        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                        }
                        // Log.i("Good",responseBody.string());
                        // System.out.print(responseBody.string().toString());//СЧИТАННЫЙ ТЕКСТ
                        String string=responseBody.string();
                        int count=0;
                        String[] strings=new String[2];
                        for (String str : string.split("\n")) {
                            Log.i("str",str);
                            strings[count]=str;
                            count++;
                        }
                        String s1;
                        String s2;
                            s1=sp.getString(notif_text, "notif_text");
                            Log.i("s1",s1);
                            s2=sp.getString(notif_url, "notif_url");
                            Log.i("s2",s2);
                            if(!(s1.equals(strings[0]) && s2.equals(strings[1]))){
                                /*Отправляем строки в уведомление*/
                                pushText(strings[0],strings[1]);

                                /*Сохранение переменных в SharedPreference*/
                                SharedPreferences.Editor e = sp.edit();
                                e.putString(notif_text, strings[0]);
                                e.putString(notif_url, strings[1]);
                                e.commit(); // не забудьте подтвердить изменения
                            }

                    }
                }
            });
        }
    }
    /**------------------------------------------------------------------**/
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
