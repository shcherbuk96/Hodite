package com.hodite.com.shcherbuk.WebActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.hodite.com.shcherbuk.ActivityManager;
import com.hodite.com.shcherbuk.Constants;
import com.hodite.com.shcherbuk.R;
import com.kobakei.ratethisapp.RateThisApp;

import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

public class WebActivity extends AppCompatActivity implements Constants, SharedPreferences.OnSharedPreferenceChangeListener {
    BottomNavigationView bottomNavigationView;
    EditText etFeedback;
    EditText etSubject;
    Button btnSubmit;
    private XWalkView web;
    private String url;
    private ImageView refresh;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web);
        // Monitor launch times and interval from installation
        RateThisApp.onCreate(this);
        // If the condition is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this);
        initWeb();
        // Регистрируем этот OnSharedPreferenceChangeListener
        Context context = getApplicationContext();
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);

        bottomNavigationView = findViewById(R.id.main_navigation_bottom_navigation_view);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
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
                        feedBackAlertDialog();

                        break;

                    case R.id.settings:
                        ActivityManager.startSettingsActivity(bottomNavigationView.getContext());

                        break;

                    case R.id.search:
                        searchAlertDialog();

                        break;
                }

                return true;
            }
        });

        loadWeb();
    }

    public void initWeb() {
        web = findViewById(R.id.web_xwalkview);
        web.setResourceClient(new ResourceClient(web));
        web.setUIClient(new UIClient(web));
        web.clearCache(true);
    }

    public void loadWeb() {
        url = getIntent().getStringExtra(KEY_INTENT);
        web.load(url, null);
    }

    private void feedBackAlertDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.feedback_title))
                .create();

        LayoutInflater inflater = this.getLayoutInflater();
        dialog.setView(inflater.inflate(R.layout.feedback, null));
        dialog.show();

        etFeedback = dialog.findViewById(R.id.etFeedback);
        etSubject = dialog.findViewById(R.id.etEmail);
        btnSubmit = dialog.findViewById(R.id.btnSubmit);

        final ImageView vk = dialog.findViewById(R.id.vk);
        final ImageView youtube = dialog.findViewById(R.id.youtube);
        final ImageView instagram = dialog.findViewById(R.id.instagram);

        vk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                socialNetwork(vkUrl);
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                socialNetwork(youtubeUrl);
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                socialNetwork(instagramUrl);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!etFeedback.getText().toString().isEmpty()) {
                    feedBackThisApp(etFeedback.getText().toString(), etSubject.getText().toString());
                } else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.feedback_toast), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchAlertDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.search_title))
                .create();

        LayoutInflater inflater = this.getLayoutInflater();
        dialog.setView(inflater.inflate(R.layout.search_dialog, null));
        dialog.show();

        final EditText etSearch = dialog.findViewById(R.id.search_dialog_edit_text);
        Button find = dialog.findViewById(R.id.search_dialog_find_btn);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String text = etSearch.getText().toString();

                if (!text.isEmpty()) {
                    url = searchDeialogUrl + text;
                    web.hasEnteredFullscreen();
                    web.load(url, null);

                    dialog.dismiss();
                }
            }
        });
    }

    private void feedBackThisApp(String text, String subject) {
        final Intent feedback = new Intent(Intent.ACTION_SEND);
        feedback.setType("text/plain");
        feedback.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{hoditeEmail});
        feedback.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        feedback.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(feedback, getResources().getString(R.string.feedback_email_client)));
    }

    private void socialNetwork(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.exit_title))
                .setMessage(getResources().getString(R.string.exit_warning))
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

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (key.equals("settings_website")) {
            if (sharedPreferences.getBoolean(key, true)) {
                FirebaseMessaging.getInstance().subscribeToTopic("WEB");

                Log.e(key, "true");
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("WEB");

                Log.e(key, "false");
            }
        }

        if (key.equals("settings_shop")) {
            if (sharedPreferences.getBoolean(key, true)) {
                FirebaseMessaging.getInstance().subscribeToTopic("SHOP");

                Log.e(key, "true");
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("SHOP");

                Log.e(key, "false");
            }
        }
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
            if (!url.contains("hodite")) {
                final Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
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
