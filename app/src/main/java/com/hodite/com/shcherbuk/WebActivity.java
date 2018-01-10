package com.hodite.com.shcherbuk;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

public class WebActivity extends AppCompatActivity implements Constants{
    XWalkView web;
    String url;
    ImageView refresh;

/**------------------------КЛАСС ДЛЯ ОБРАБОТКИ XWALKVIEW-----------------------------**/
    class ResourceClient extends XWalkResourceClient {

        public ResourceClient(XWalkView xwalkView) {
            super(xwalkView);
        }

        public void onLoadStarted(XWalkView view, String url) {
            super.onLoadStarted(view, url);
            Log.d(TAG_WEB, "Load Started:" + url);
        }

        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);
            Log.d(TAG_WEB, "Load Finished:" + url);
        }

        public void onProgressChanged(XWalkView view, int progressInPercent) {
            super.onProgressChanged(view, progressInPercent);
            Log.d(TAG_WEB, "Loading Progress:" + progressInPercent);
        }

        public WebResourceResponse shouldInterceptLoadRequest(XWalkView view, String url) {
            Log.d(TAG_WEB, "Intercept load request");
            return super.shouldInterceptLoadRequest(view, url);
        }

        public void onReceivedLoadError(XWalkView view, int errorCode, String description,
                                        String failingUrl) {

            refresh.setVisibility(View.VISIBLE);

            Log.d(TAG_WEB, "Load Failed:" + description);

            Toast.makeText(getApplicationContext(),"Проверьте подключение к интернету",Toast.LENGTH_LONG).show();

            super.onReceivedLoadError(view, errorCode, description, failingUrl);
        }

        public void onDocumentLoadedInFrame(XWalkView view, long frameId) {
            // TODO Auto-generated method stub
            super.onDocumentLoadedInFrame(view, frameId);
            Log.d(TAG_WEB, "onDocumentLoadedInFrame frameId: " + frameId);
        }

        public void doUpdateVisitedHistory(XWalkView view, String url,
                                           boolean isReload) {
            // TODO Auto-generated method stub
            Log.d(TAG_WEB, "doUpdateVisitedHistory url: " + url + "isReload: "+isReload);
            super.doUpdateVisitedHistory(view, url, isReload);
        }
    }

    class UIClient extends XWalkUIClient {

        public UIClient(XWalkView xwalkView) {
            super(xwalkView);
        }

        public void onJavascriptCloseWindow(XWalkView view) {
            super.onJavascriptCloseWindow(view);
            Log.d(TAG_WEB, "Window closed.");
        }

        public boolean onJavascriptModalDialog(XWalkView view, JavascriptMessageType type,
                                               String url,
                                               String message, String defaultValue, XWalkJavascriptResult result) {
            Log.d(TAG_WEB, "Show JS dialog.");
            return super.onJavascriptModalDialog(view, type, url, message, defaultValue, result);
        }

        public void onFullscreenToggled(XWalkView view, boolean enterFullscreen) {
            super.onFullscreenToggled(view, enterFullscreen);
            if (enterFullscreen) {
                Log.d(TAG_WEB, "Entered fullscreen.");
            } else {
                Log.d(TAG_WEB, "Exited fullscreen.");
            }
        }

        public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile,
                                    String acceptType, String capture) {
            super.openFileChooser(view, uploadFile, acceptType, capture);
            Log.d(TAG_WEB, "Opened file chooser.");
        }

        public void onScaleChanged(XWalkView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
        }

        @Override
        public void onPageLoadStarted(XWalkView view, String url) {
            // TODO Auto-generated method stub
            super.onPageLoadStarted(view, url);
            Log.d(TAG_WEB, "Page Load Started. url: " + url);
        }

        @Override
        public void onPageLoadStopped(XWalkView view, String url,
                                      LoadStatus status) {
            // TODO Auto-generated method stub
            super.onPageLoadStopped(view, url, status);
            Log.d(TAG_WEB, "Page Load Stopped. url: " + url + " status: " + status);
        }
    }

/**------------------------------------------------------------------------------**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /**Растянуть окно на весь экран**/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_web);

//        if(!isMyServiceRunning(MyService.class)){
//            startService(new Intent(this,MyService.class));
//        }

        refresh=(ImageView)findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWeb();
                refresh.setVisibility(View.INVISIBLE);
            }
        });


       // Toast.makeText(this,"Данное приложение может взымать большое количество интернет-трафика",Toast.LENGTH_LONG).show();

        loadWeb();

    }



    public void loadWeb(){


        //Intent intent=getIntent();
        url=getIntent().getStringExtra(KEY_INTENT);

        web=(XWalkView) findViewById(R.id.web_xwalkview);
        web.setResourceClient(new ResourceClient(web));
        web.setUIClient(new UIClient(web));
        web.clearCache(true);
        web.load(url, null);
        //progressBar.setVisibility(ProgressBar.INVISIBLE);

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



    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        overridePendingTransition(R.anim.fadein,R.anim.fadeout); //Переход с затуханием
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    @Override
    protected void onStop() {
        super.onStop();

    }
}
