package com.pdyjak.powerampwear;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.firebase.FirebaseApp;

public class App extends Application {
    private static final String ONBOARDING_COMPLETED_KEY = "onboarding_completed";
    private static final String SHOW_ALBUM_ART_KEY = "show_albumart";
    private static final String WAKELOCK_ON_SONG_CHANGE = "albumart_wakelock";

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

    public boolean shouldShowAlbumArt() {
        return getPrefs().getBoolean(SHOW_ALBUM_ART_KEY, true);
    }

    public void saveShouldShowAlbumArt(boolean value) {
        getPrefs().edit().putBoolean(SHOW_ALBUM_ART_KEY, value).apply();
    }

    public boolean shouldWakeWhenChangingSongs() {
        return getPrefs().getBoolean(WAKELOCK_ON_SONG_CHANGE, shouldShowAlbumArt());
    }

    public void saveShouldWakeWhenChangingSongs(boolean value) {
        getPrefs().edit().putBoolean(WAKELOCK_ON_SONG_CHANGE, value).apply();
    }
}
