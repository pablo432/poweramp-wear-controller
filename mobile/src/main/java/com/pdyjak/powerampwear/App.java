package com.pdyjak.powerampwear;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.firebase.FirebaseApp;

public class App extends Application {
    private static final String ONBOARDING_COMPLETED_KEY = "onboarding_completed";

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.ENABLE_CRASH_REPORTING) FirebaseApp.initializeApp(this);
    }

    @NonNull
    private SharedPreferences getPrefs() {
        return getSharedPreferences(getString(R.string.prefs_key), Context.MODE_PRIVATE);
    }

    public boolean isOnboardingCompleted() {
        return getPrefs().getBoolean(ONBOARDING_COMPLETED_KEY, false);
    }

    public void saveOnboardingCompleted() {
        getPrefs().edit().putBoolean(ONBOARDING_COMPLETED_KEY, true).apply();
    }
}
