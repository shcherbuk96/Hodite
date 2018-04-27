package com.hodite.com.shcherbuk;

import android.content.Context;
import android.content.Intent;

import com.hodite.com.shcherbuk.WebActivity.WebActivity;

public class ActivityManager implements Constants {

    public static void startWebActivity(final Context context, final String url) {
        final Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(KEY_INTENT, url);
        context.startActivity(intent);
    }
}
