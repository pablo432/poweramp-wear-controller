package com.pdyjak.powerampwear;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;
import com.pdyjak.powerampwearcommon.DataApiConstants;

import java.util.concurrent.TimeUnit;

public class WearableCrashInterceptorService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        DataApi.DataListener {

    private static final long RECONNECT_DELAY = TimeUnit.HOURS.toMillis(1);

    private static class WearableException extends Exception {
        private WearableException(String message) {
            super(message);
        }
    }

    private Handler mHandler;
    private GoogleApiClient mGoogleApiClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        if (BuildConfig.ENABLE_CRASH_REPORTING) FirebaseApp.initializeApp(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient.connect();
            }
        }, RECONNECT_DELAY);
    }

    @Override
    public void onDataChanged(DataEventBuffer eventBuffer) {
        for (DataEvent event : eventBuffer) {
            switch (event.getType()) {
                case DataEvent.TYPE_CHANGED:
                    processDataEvent(event);
                    break;
            }
        }
    }

    private void processDataEvent(@NonNull DataEvent event) {
        DataItem item = event.getDataItem();
        DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
        switch (item.getUri().getPath()) {
            case DataApiConstants.UNHANDLED_EXCEPTION_PATH:
                String stackTrace = dataMap.getString(DataApiConstants.STACKTRACE_KEY);
                if (BuildConfig.ENABLE_CRASH_REPORTING) {
                    FirebaseCrash.report(new WearableException(stackTrace));
                }
                Wearable.DataApi.deleteDataItems(mGoogleApiClient, item.getUri());
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }
}
