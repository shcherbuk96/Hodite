package com.hodite.com.shcherbuk.VideoTutorial;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.hodite.com.shcherbuk.ActivityManager;
import com.hodite.com.shcherbuk.Constants;
import com.hodite.com.shcherbuk.MainActivity.MainActivity;
import com.hodite.com.shcherbuk.R;
import com.hodite.com.shcherbuk.WebActivity.WebActivity;

public class VideoTutorial extends YouTubeBaseActivity implements Constants,YouTubePlayer.OnInitializedListener {

    private YouTubePlayer youTubePlayer;
    private SharedPreferences sp;
    private Context context;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**Растянуть окно на весь экран**/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video_tutorial);

        context=this;

        FirebaseMessaging.getInstance().subscribeToTopic("WEB");
        FirebaseMessaging.getInstance().subscribeToTopic("SHOP");

        sp = getSharedPreferences(CHECK_SETTINGS,
                Context.MODE_PRIVATE);

        // проверяем, первый ли раз открывается программа
        final boolean hasVisited = sp.getBoolean(hasWathed, false);

        if (!hasVisited) {
            final YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
            youTubePlayerView.initialize(API_KEY, this);
            final SharedPreferences.Editor e = sp.edit();
            e.putBoolean(hasWathed, true);
            e.commit(); // не забудьте подтвердить изменения
        }
/*        else{
            ActivityManager.startWebActivity(context,Constants.URL_HODITE_COM);
            finish();
        }*/
    }

    @Override
    public void onInitializationFailure(final Provider provider, final YouTubeInitializationResult result) {
        Toast.makeText(this, "Произошла ошибка!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(final Provider provider, final YouTubePlayer player, final boolean wasRestored) {
        youTubePlayer=player;
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }

    }

    private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {
        @Override
        public void onBuffering(final boolean arg0) {
        }
        @Override
        public void onPaused() {
        }
        @Override
        public void onPlaying() {
        }
        @Override
        public void onSeekTo(final int arg0) {
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
        public void onError(final ErrorReason arg0) {
        }
        @Override
        public void onLoaded(final String arg0) {
            if(!TextUtils.isEmpty(arg0) && youTubePlayer != null)
                youTubePlayer.play(); //auto play
        }
        @Override
        public void onLoading() {
        }
        @Override
        public void onVideoEnded() {
            final SharedPreferences.Editor e = sp.edit();
            e.putBoolean(hasWathed, true);
            e.commit(); // не забудьте подтвердить изменения

            ActivityManager.startWebActivity(context,Constants.URL_HODITE_COM);
            finish();
        }
        @Override
        public void onVideoStarted() {
        }
    };

    @Override
    public void onBackPressed() {
        if(youTubePlayer!=null){
            youTubePlayer.pause();
        }

        new AlertDialog.Builder(this)
                .setMessage("Вы не хотите смотреть обучающее видео?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        youTubePlayer.play();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface arg0, final int arg1) {
                        final SharedPreferences.Editor e = sp.edit();
                        e.putBoolean(hasWathed, true);
                        e.commit(); // не забудьте подтвердить изменения

                        ActivityManager.startWebActivity(context,Constants.URL_HODITE_COM);
                        finish();

                    }
                }).create().show();
    }
}
