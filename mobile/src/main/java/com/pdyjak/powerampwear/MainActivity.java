package com.pdyjak.powerampwear;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private class ServiceConnectionImpl implements ServiceConnection {
        @Nullable
        private IBackgroundService mBinder;
        @Nullable
        private Boolean mShouldShowAlbumArt;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = IBackgroundService.Stub.asInterface(service);
            if (mShouldShowAlbumArt != null) {
                try {
                    mBinder.setShowAlbumArt(mShouldShowAlbumArt);
                    mShouldShowAlbumArt = null;
                } catch (RemoteException e) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
        }

        void setShouldShowAlbumArt(boolean shouldShow) {
            if (mBinder == null) {
                mShouldShowAlbumArt = shouldShow;
            } else {
                try {
                    mBinder.setShowAlbumArt(shouldShow);
                } catch (RemoteException e) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                }
            }
        }
    }

    @NonNull
    private final ServiceConnectionImpl mServiceConnection = new ServiceConnectionImpl();
    @Nullable
    private Intent mServiceIntent;

    private boolean mServiceStarted;
    private boolean mServiceBound;
    private TextView mPowerampInstalledTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (BuildConfig.ENABLE_CRASH_REPORTING) {
            startService(new Intent(this, WearableCrashInterceptorService.class));
        }
        mServiceIntent = new Intent(this, BackgroundService.class);
        mPowerampInstalledTextView = (TextView) findViewById(R.id.poweramp_installed_textview);
        findViewById(R.id.restart_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartService();
            }
        });
        final CheckBox albumArtCheckbox = (CheckBox) findViewById(R.id.albumart_checkbox);
        albumArtCheckbox.setChecked(((App) getApplication()).shouldShowAlbumArt());
        albumArtCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((App) getApplication()).saveShouldShowAlbumArt(isChecked);
                mServiceConnection.setShouldShowAlbumArt(isChecked);
            }
        });
        findViewById(R.id.albumart_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumArtCheckbox.setChecked(!albumArtCheckbox.isChecked());
            }
        });
    }

    private void restartService() {
        if (!Utils.isPowerampInstalled(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.restart_service_pointless,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        stopService();
        startServiceIfNeeded();
        Toast.makeText(MainActivity.this, R.string.service_restarted,
                Toast.LENGTH_SHORT).show();
    }

    private void startServiceIfNeeded() {
        if (mServiceStarted) {
            bindServiceIfNeeded();
            return;
        }
        mServiceStarted = true;
        startService(mServiceIntent);
        bindServiceIfNeeded();
    }

    private void bindServiceIfNeeded() {
        if (mServiceBound) return;
        mServiceBound = true;
        bindService(mServiceIntent, mServiceConnection,
                Context.BIND_NOT_FOREGROUND | Context.BIND_ABOVE_CLIENT);
    }

    private void unbindServiceIfNeeded() {
        if (!mServiceBound) return;
        mServiceBound = false;
        unbindService(mServiceConnection);
    }

    private void stopService() {
        if (!mServiceStarted) return;
        mServiceStarted = false;
        unbindServiceIfNeeded();
        stopService(mServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean installed = Utils.isPowerampInstalled(this);
        if (installed) {
            mPowerampInstalledTextView.setText(getString(R.string.just_launch_smartwatch));
            mPowerampInstalledTextView.setTextColor(Color.BLACK);
            startServiceIfNeeded();
        } else {
            mPowerampInstalledTextView.setText(getString(R.string.poweramp_not_installed));
            mPowerampInstalledTextView.setTextColor(
                    ContextCompat.getColor(this, R.color.colorAccent));
            stopService();
        }
    }

    @Override
    protected void onPause() {
        unbindServiceIfNeeded();
        super.onPause();
    }
}
