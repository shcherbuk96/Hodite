package com.hodite.com.shcherbuk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubePlayerView;

public class StartActivity extends AppCompatActivity implements Constants {

    ImageView start_logo;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**Растянуть окно на весь экран**/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);

        start_logo=(ImageView)findViewById(R.id.start_logo);

        Animation animRotateIn_icon = AnimationUtils.loadAnimation(this,
                R.anim.fadein);
        start_logo.startAnimation(animRotateIn_icon);
        startApp();
    }

    private void startApp(){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                startActivity(new Intent(getApplicationContext(), VideoTutorial.class));
                overridePendingTransition(R.anim.fadein,R.anim.fadeout); //Переход с затуханием
                finish();


                //overridePendingTransition(R.anim.shrink_and_rotate_b,R.anim.shrink_and_rotate_a); //Переход с затуханием
                //overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out); //Шторка снизу вверх
                //overridePendingTransition(R.anim.activity_down_up_enter,R.anim.activity_down_up_close_enter); //Шторка
                //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left); //Переход Слайд-Шоу

            }
        }, 3*1000);

/*        Timer myTimer = new Timer(); // Создаем таймер
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {

                Intent intent=new Intent(getApplicationContext(),WebActivity.class);
                intent.putExtra(KEY_INTENT,URL_MY_WEBSITE);
                //Запуск Браузера
                //startActivity(intent);

                //Запуск Main

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                //finish();
            }
        }, 3L * 1000);*/
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
}
