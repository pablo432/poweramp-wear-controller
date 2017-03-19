package com.pdyjak.powerampwear;

import android.support.annotation.NonNull;

import com.google.android.gms.wearable.MessageEvent;

public interface MessageListener {
    void onMessageReceived(@NonNull MessageEvent messageEvent);
}
