package com.pdyjak.powerampwear;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.pdyjak.powerampwearcommon.DataApiConstants;

import java.io.PrintWriter;
import java.io.StringWriter;

// Sends uncaught exceptions to the handheld application to allow sending them further to Firebase.
public class CrashDeliveryService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String EXCEPTION_KEY = "exception";

    public static void launch(@NonNull Throwable throwable, @NonNull Context context) {
        Intent intent = new Intent(context, CrashDeliveryService.class);
        intent.putExtra(EXCEPTION_KEY, throwable);
        context.startService(intent);
    }

    private Throwable mUncaughtThrowable;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUncaughtThrowable = (Throwable) intent.getSerializableExtra(EXCEPTION_KEY);
        return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        StringWriter sw = new StringWriter();
        mUncaughtThrowable.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        final PutDataMapRequest request = PutDataMapRequest.create(
                DataApiConstants.UNHANDLED_EXCEPTION_PATH);
        request.setUrgent();
        DataMap dataMap = request.getDataMap();
        dataMap.putString(DataApiConstants.STACKTRACE_KEY, stackTrace);
        PutDataRequest putDataRequest = request.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult result) {
                        // TODO: Same stuff as in onConnectionFailed
                        mGoogleApiClient.disconnect();
                        stopSelf();
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Possible TODO: Hopefully an edge case, but it would be nice to store stacktrace somewhere
        // and resend it on next opportunity.
        stopSelf();
    }
}
