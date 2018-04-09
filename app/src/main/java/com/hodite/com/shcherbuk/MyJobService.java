package com.hodite.com.shcherbuk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.hodite.com.shcherbuk.WebActivity.WebActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by Администратор on 08.01.2018.
 */

public class MyJobService extends JobService implements Constants{
    // BackgroundTask backgroundTask;
    NotificationManager nm;
    SharedPreferences sp;

    private final OkHttpClient client = new OkHttpClient();

    Request request = new Request.Builder()
            .url(URL_TEXT_TXT)
            .build();

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        sp = getSharedPreferences(CHECK_SETTINGS,
                Context.MODE_PRIVATE);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"OnFailure",Toast.LENGTH_SHORT).show();
                jobFinished(jobParameters,true);
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
                    if(!(s1.trim().equals(strings[0].trim()) && s2.trim().equals(strings[1].trim()))){
/*Отправляем строки в уведомление*/
                        pushText(strings[0],strings[1]);

/*Сохранение переменных в SharedPreference*/
                        SharedPreferences.Editor e = sp.edit();
                        e.putString(notif_text, strings[0]);
                        e.putString(notif_url, strings[1]);
                        e.commit(); // не забудьте подтвердить изменения
                    }
                    jobFinished(jobParameters,true);
//                    Toast.makeText(getApplicationContext(),"OnFinished",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters jobParameters) {
        return true;
    }

    public void pushText(String str1,String str2){
//String url="http://"+str2;

        Intent intent=new Intent(this,WebActivity.class);
        intent.putExtra(KEY_INTENT,str2);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
// оставим только самое необходимое
        builder.setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis()) //Время уведомления
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
}
