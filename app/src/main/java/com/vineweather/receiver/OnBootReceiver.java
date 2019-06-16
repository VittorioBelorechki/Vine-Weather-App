package com.vineweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vineweather.widget.MaPaWidgetProvider;

public class OnBootReceiver extends BroadcastReceiver {

    public OnBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            MaPaWidgetProvider.startService(context);
        }
    }
}
