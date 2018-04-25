package com.hodite.com.shcherbuk.SplashActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hodite.com.shcherbuk.ActivityManager;
import com.hodite.com.shcherbuk.Constants;

public class SplashScreen extends AppCompatActivity implements Constants {
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkMessages();
            }
        }, Constants.WAIT);
    }


    private void checkMessages() {
        if (getIntent() == null || getIntent().getStringExtra("URL") == null) {
            // проверяем, первый ли раз открывается программа

            sp = getSharedPreferences(CHECK_SETTINGS,
                    Context.MODE_PRIVATE);
            final boolean hasVisited = sp.getBoolean(hasWathed, false);

            if (hasVisited) {
                ActivityManager.startWebActivity(this, Constants.URL_HODITE_COM);
                finish();
            } else {
                FirebaseMessaging.getInstance().subscribeToTopic("WEB");
                FirebaseMessaging.getInstance().subscribeToTopic("SHOP");
                final SharedPreferences.Editor e = sp.edit();
                e.putBoolean(hasWathed, true);
                e.commit(); // не забудьте подтвердить изменения
//                    ActivityManager.startVideoTutorial(this);
//                    finish();
            }

        } else if (getIntent().getStringExtra("URL") != null) {
            ActivityManager.startWebActivity(this, getIntent().getStringExtra("URL"));
            finish();
        }
    }
}
