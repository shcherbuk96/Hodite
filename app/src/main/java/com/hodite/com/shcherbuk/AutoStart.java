package com.hodite.com.shcherbuk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Администратор on 29.12.2017.
 */

public class AutoStart extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,MyService.class));
    }
}
