package com.hodite.com.shcherbuk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.hodite.com.shcherbuk.Constants.CHECK_SETTINGS;
import static com.hodite.com.shcherbuk.Constants.notif_text;
import static com.hodite.com.shcherbuk.Constants.notif_url;


/**
 * Created by Администратор on 08.01.2018.
 */

public class MyJobService extends com.firebase.jobdispatcher.JobService implements Constants{
//    BackgroundTask backgroundTask;
    NotificationManager nm;
    SharedPreferences sp;

    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters jobParameters) {
        sp = getSharedPreferences(CHECK_SETTINGS,
                Context.MODE_PRIVATE);

        //Toast.makeText(getApplicationContext(),"onStart",Toast.LENGTH_SHORT).show();
        push();
        jobFinished(jobParameters,true);




        /*backgroundTask=new BackgroundTask(){
            @Override
            protected void onPostExecute(String s) {

                jobFinished(jobParameters,false);
            }
        };
        backgroundTask.execute();*/
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters jobParameters) {
        //Toast.makeText(getApplicationContext(),"onStop",Toast.LENGTH_SHORT).show();

        return true;
    }

/*    public static class BackgroundTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {

            return null;
        }
    }*/
    public void push() {
        try {
            new MyJobService.asynchronousGet().run();
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
                        if(!(s1.trim().equals(strings[0].trim()) && s2.trim().equals(strings[1].trim()))){
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
}
