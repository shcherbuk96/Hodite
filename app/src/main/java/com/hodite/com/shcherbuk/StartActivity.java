package com.hodite.com.shcherbuk;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.youtube.player.YouTubePlayerView;

public class StartActivity extends AppCompatActivity implements Constants {
    private static final String JOB_TAG = "my_job_tag";
    FirebaseJobDispatcher firebaseJobDispatcher;
    RelativeLayout relativeLayout;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**Растянуть окно на весь экран**/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);

        relativeLayout=(RelativeLayout)findViewById(R.id.startLayout);

        Animation animRotateIn_icon = AnimationUtils.loadAnimation(this,
                R.anim.fadein);
        relativeLayout.startAnimation(animRotateIn_icon);



//        if(!isMyServiceRunning(MyService.class)){
//            startService(new Intent(this,MyService.class));
//        }
//        Intent alarm = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);

//        startService(new Intent(this, MyTimerService.class));
        firebaseJobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));
        onStartJob();

        startApp();
    }

    public void onStartJob(){
        Job job=firebaseJobDispatcher.newJobBuilder().
                setService(MyJobService.class).
                setLifetime(Lifetime.FOREVER).
                setRecurring(true).
                setTag(JOB_TAG).
                setTrigger(Trigger.executionWindow(10,20)).
                setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL).
                setConstraints(Constraint.ON_ANY_NETWORK).
                setReplaceCurrent(false).build();
        firebaseJobDispatcher.mustSchedule(job);
        Toast.makeText(getApplicationContext(),"JobSheduled",Toast.LENGTH_SHORT).show();
    }
    private void startApp(){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                startActivity(new Intent(getApplicationContext(), VideoTutorial.class));
                overridePendingTransition(R.anim.fadein,R.anim.fadeout); //Переход с затуханием
                finish();

            }
        }, 3*1000);
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        startApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startApp();
    }

    @Override
    public void onBackPressed() {

    }

    /**-----------------------------ПРОВЕРКА НА ЗАПУСК СЕРВИСА--------------------**/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    /*----------------------------------------------------------------------------------*/
}
