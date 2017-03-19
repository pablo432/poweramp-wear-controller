package com.pdyjak.powerampwear;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private boolean mServiceStarted;
    private TextView mPowerampInstalledTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.ENABLE_CRASH_REPORTING) {
            startService(new Intent(this, WearableCrashInterceptorService.class));
        }
        setContentView(R.layout.activity_main);
        boolean installed = Utils.isPowerampInstalled(this);
        mPowerampInstalledTextView = (TextView) findViewById(R.id.poweramp_installed_textview);
        final Intent intent = new Intent(this, BackgroundService.class);
        if (installed) {
            startService(intent);
            mServiceStarted = true;
        }
        findViewById(R.id.restart_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isPowerampInstalled(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, R.string.restart_service_pointless,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mServiceStarted) stopService(intent);
                startService(intent);
                Toast.makeText(MainActivity.this, R.string.service_restarted,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, BackgroundService.class);
        boolean installed = Utils.isPowerampInstalled(this);
        if (installed) {
            mPowerampInstalledTextView.setText(getString(R.string.just_launch_smartwatch));
            mPowerampInstalledTextView.setTextColor(Color.BLACK);
            if (!mServiceStarted) {
                mServiceStarted = true;
                startService(intent);
            }
        } else {
            mPowerampInstalledTextView.setText(getString(R.string.poweramp_not_installed));
            mPowerampInstalledTextView.setTextColor(
                    ContextCompat.getColor(this, R.color.colorAccent));
            if (mServiceStarted) {
                mServiceStarted = false;
                stopService(intent);
            }
        }
    }
}
