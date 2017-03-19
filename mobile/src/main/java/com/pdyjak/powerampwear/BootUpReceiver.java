package com.pdyjak.powerampwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {
        context.startService(new Intent(context, BackgroundService.class));
    }
}
