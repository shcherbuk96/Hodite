package com.hodite.com.shcherbuk.SplashActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hodite.com.shcherbuk.ActivityManager;
import com.hodite.com.shcherbuk.Constants;

public class SplashScreen extends AppCompatActivity implements Constants {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkMessages();
    }

    private void checkMessages() {

        if (getIntent() != null) {

            Log.d("Intent", "!=null");

            if (getIntent().getStringExtra("URL") == null) {

                Log.d("extras", "=null");

                ActivityManager.startVideoTutorial(this);
                finish();
            } else if (getIntent().getStringExtra("URL") != null) {

                Log.d("extras", String.valueOf(getIntent().getExtras()));

                ActivityManager.startWebActivity(this, getIntent().getStringExtra("URL"));
                finish();
            }

        } else if (getIntent() == null) {

            Log.d("Intent", "=null");

            ActivityManager.startVideoTutorial(this);
            finish();

        }
    }
}
