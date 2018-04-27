package com.hodite.com.shcherbuk.WebActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hodite.com.shcherbuk.Constants;
import com.hodite.com.shcherbuk.R;

import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.util.Arrays;
import java.util.List;

public class WebActivity extends AppCompatActivity implements Constants {
    BottomNavigationView bottomNavigationView;
    EditText etFeedback;
    Button btnSubmit;
    private XWalkView web;
    private String url;
    private ImageView refresh;
    private SharedPreferences sp;
    private boolean[] mCheckedItems;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_web);


        bottomNavigationView = findViewById(R.id.main_navigation_bottom_navigation_view);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                loadWeb();
                refresh.setVisibility(View.INVISIBLE);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.feedback:
                        //feedBackThisApp();
                        feedBackAlertDialog();
                        break;
                    case R.id.rating:
                        rateThisApp();

                        break;
                    case R.id.settings:
                        settingsThisApp();

                        break;
                }

                return true;
            }
        });
        loadWeb();

    }

    public void loadWeb() {
        url = getIntent().getStringExtra(KEY_INTENT);
        web = findViewById(R.id.web_xwalkview);
        web.setResourceClient(new ResourceClient(web));
        web.setUIClient(new UIClient(web));
        web.clearCache(true);
        web.load(url, null);
    }

    private void feedBackAlertDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("FeedBack")
                .create();

        LayoutInflater inflater = this.getLayoutInflater();
        dialog.setView(inflater.inflate(R.layout.feedback, null));
        dialog.show();
        etFeedback = dialog.findViewById(R.id.etFeedback);
        btnSubmit = dialog.findViewById(R.id.btnSubmit);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!etFeedback.getText().toString().isEmpty()) {
                    feedBackThisApp(etFeedback.getText().toString());
                } else
                    Toast.makeText(getApplicationContext(), "Напишите отзыв", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void feedBackThisApp(String text) {
        final Intent feedback = new Intent(android.content.Intent.ACTION_SEND);
        feedback.setType("text/html");
        feedback.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"evgenus196@gmail.com"});
        feedback.putExtra(android.content.Intent.EXTRA_SUBJECT, "shcherbuk96@mail.ru");
        feedback.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(feedback, "FeedBack"));
    }

    private void settingsThisApp() {
        final String[] items = {" Получать уведомления об обновлениях сайта ", " Получать уведомления об акциях магазинов "};

        final List<String> colorsList = Arrays.asList(items);

        sp = getSharedPreferences(CHECK_SETTINGS,
                Context.MODE_PRIVATE);

        final boolean webSite = sp.getBoolean(notifWebSite, true);
        final boolean shops = sp.getBoolean(notifShops, true);

        mCheckedItems = new boolean[]{webSite, shops};


        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Настройки")
                .setMultiChoiceItems(items, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int indexSelected, final boolean isChecked) {
                        mCheckedItems[indexSelected] = isChecked;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int id) {
                        final SharedPreferences.Editor e = sp.edit();
                        e.putBoolean(notifWebSite, mCheckedItems[0]);
                        e.putBoolean(notifShops, mCheckedItems[1]);
                        e.commit(); // не забудьте подтвердить изменения

                        if (mCheckedItems[0]) {
                            FirebaseMessaging.getInstance().subscribeToTopic("WEB");
                        } else {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("WEB");
                        }

                        if (mCheckedItems[1]) {
                            FirebaseMessaging.getInstance().subscribeToTopic("SHOP");
                        } else {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("SHOP");
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int id) {
                        //  Your code when user clicked on Cancel
                        dialog.cancel();
                    }
                }).create();
        dialog.show();
    }

    private void rateThisApp() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface arg0, final int arg1) {
                        //эмулируем нажатие на HOME, сворачивая приложение
                        final Intent i = new Intent(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    }
                }).create().show();
    }

    /**
     * ------------------------КЛАСС ДЛЯ ОБРАБОТКИ XWALKVIEW-----------------------------
     **/
    class ResourceClient extends XWalkResourceClient {

        ResourceClient(final XWalkView xwalkView) {
            super(xwalkView);
        }

        public void onLoadStarted(final XWalkView view, final String url) {
            super.onLoadStarted(view, url);
            Log.d(TAG_WEB, "Load Started:" + url);
        }

        public void onLoadFinished(final XWalkView view, final String url) {
            super.onLoadFinished(view, url);
            Log.d(TAG_WEB, "Load Finished:" + url);
        }

        public void onProgressChanged(final XWalkView view, final int progressInPercent) {
            super.onProgressChanged(view, progressInPercent);
            Log.d(TAG_WEB, "Loading Progress:" + progressInPercent);
        }

        public WebResourceResponse shouldInterceptLoadRequest(final XWalkView view, final String url) {
            Log.d(TAG_WEB, "Intercept load request");
            return super.shouldInterceptLoadRequest(view, url);
        }

        public void onReceivedLoadError(final XWalkView view, final int errorCode, final String description,
                                        final String failingUrl) {

            refresh.setVisibility(View.VISIBLE);

            Log.d(TAG_WEB, "Load Failed:" + description);

            Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_LONG).show();

            super.onReceivedLoadError(view, errorCode, description, failingUrl);
        }

        public void onDocumentLoadedInFrame(final XWalkView view, final long frameId) {
            // TODO Auto-generated method stub
            super.onDocumentLoadedInFrame(view, frameId);
            Log.d(TAG_WEB, "onDocumentLoadedInFrame frameId: " + frameId);
        }

        public void doUpdateVisitedHistory(final XWalkView view, final String url,
                                           final boolean isReload) {
            // TODO Auto-generated method stub
            Log.d(TAG_WEB, "doUpdateVisitedHistory url: " + url + "isReload: " + isReload);
            super.doUpdateVisitedHistory(view, url, isReload);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final XWalkView view, final String url) {
/*        if(url.contains("vk.com")||url.contains("instagram")){
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

            return true;
        }

        return super.shouldOverrideUrlLoading(view, url);*/
            final Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

            return true;
        }
    }

    class UIClient extends XWalkUIClient {

        UIClient(final XWalkView xwalkView) {
            super(xwalkView);
        }

        public void onJavascriptCloseWindow(final XWalkView view) {
            super.onJavascriptCloseWindow(view);
            Log.d(TAG_WEB, "Window closed.");
        }

        public boolean onJavascriptModalDialog(final XWalkView view, final JavascriptMessageType type,
                                               final String url,
                                               final String message, final String defaultValue, final XWalkJavascriptResult result) {
            Log.d(TAG_WEB, "Show JS dialog.");
            return super.onJavascriptModalDialog(view, type, url, message, defaultValue, result);
        }

        public void onFullscreenToggled(final XWalkView view, final boolean enterFullscreen) {
            super.onFullscreenToggled(view, enterFullscreen);
            if (enterFullscreen) {
                Log.d(TAG_WEB, "Entered fullscreen.");
            } else {
                Log.d(TAG_WEB, "Exited fullscreen.");
            }
        }

        public void openFileChooser(final XWalkView view, final ValueCallback<Uri> uploadFile,
                                    final String acceptType, final String capture) {
            super.openFileChooser(view, uploadFile, acceptType, capture);
            Log.d(TAG_WEB, "Opened file chooser.");
        }

        public void onScaleChanged(final XWalkView view, final float oldScale, final float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
        }

        @Override
        public void onPageLoadStarted(final XWalkView view, final String url) {
            // TODO Auto-generated method stub
            super.onPageLoadStarted(view, url);
            Log.d(TAG_WEB, "Page Load Started. url: " + url);
        }

        @Override
        public void onPageLoadStopped(final XWalkView view, final String url,
                                      final LoadStatus status) {
            // TODO Auto-generated method stub
            super.onPageLoadStopped(view, url, status);
            Log.d(TAG_WEB, "Page Load Stopped. url: " + url + " status: " + status);
        }
    }

}
