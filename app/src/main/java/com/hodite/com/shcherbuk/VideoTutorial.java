package com.hodite.com.shcherbuk;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.messaging.FirebaseMessaging;

public class VideoTutorial extends YouTubeBaseActivity implements Constants,YouTubePlayer.OnInitializedListener {

    YouTubePlayer youTubePlayer;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**Растянуть окно на весь экран**/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video_tutorial);

        FirebaseMessaging.getInstance().subscribeToTopic("WEB");
        FirebaseMessaging.getInstance().subscribeToTopic("SHOP");

        sp = getSharedPreferences(CHECK_SETTINGS,
                Context.MODE_PRIVATE);

        // проверяем, первый ли раз открывается программа
        boolean hasVisited = sp.getBoolean(hasWathed, false);

        if (!hasVisited) {
            YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
            youTubePlayerView.initialize(API_KEY, this);
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean(hasWathed, true);
            e.commit(); // не забудьте подтвердить изменения
        }
        else{
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            //loadWebActivity();
        }


    }
    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Произошла ошибка!", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
/** add listeners to YouTubePlayer instance **/
        youTubePlayer=player;
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
/** Start buffering **/
        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
    }

    private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }
        @Override
        public void onPaused() {
        }
        @Override
        public void onPlaying() {
        }
        @Override
        public void onSeekTo(int arg0) {
        }
        @Override
        public void onStopped() {
        }
    };
    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }
        @Override
        public void onError(ErrorReason arg0) {
        }
        @Override
        public void onLoaded(String arg0) {
            if(!TextUtils.isEmpty(arg0) && youTubePlayer != null)
                youTubePlayer.play(); //auto play
        }
        @Override
        public void onLoading() {
        }
        @Override
        public void onVideoEnded() {
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean(hasWathed, true);
            e.commit(); // не забудьте подтвердить изменения

            //loadWebActivity();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        @Override
        public void onVideoStarted() {
        }
    };

    @Override
    public void onBackPressed() {
        youTubePlayer.pause();
        new AlertDialog.Builder(this)
                .setMessage("Вы не хотите смотреть обучающее видео?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        youTubePlayer.play();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences.Editor e = sp.edit();
                        e.putBoolean(hasWathed, true);
                        e.commit(); // не забудьте подтвердить изменения

                        //loadWebActivity();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }).create().show();
    }

    public void loadWebActivity(){
        Intent intent=new Intent(getApplicationContext(),WebActivity.class);
        intent.putExtra(KEY_INTENT,URL_HODITE_COM);
        //Запуск Браузера
        startActivity(intent);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout); //Переход с затуханием
        finish();
    }
}
