package com.pdyjak.powerampwear;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.pdyjak.powerampwear.music_browser.MusicLibraryCache;
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;
import com.pdyjak.powerampwear.settings.SettingsManager;

public class App extends Application implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
    private SettingsManager mSettingsManager;
    private MusicLibraryCache mMusicLibraryCache;
    private MusicLibraryNavigator mMusicLibraryNavigator;
    private MessageExchangeHelper mMessageExchangeHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mSettingsManager = new SettingsManager(
                getSharedPreferences(getString(R.string.prefs_file_key), Context.MODE_PRIVATE));
        mMusicLibraryCache = new MusicLibraryCacheImpl();
        mMusicLibraryNavigator = new MusicLibraryNavigatorImpl();
        mMessageExchangeHelper = new MessageExchangeHelper(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mMessageExchangeHelper.dispose();
    }

    public void onResume() {
        // Called by MainActivity - it's good as long as this is one-activity application
        mMessageExchangeHelper.onResume();
    }

    public void onPause() {
        // Called by MainActivity - it's good as long as this is one-activity application
        mMessageExchangeHelper.onPause();
    }

    @NonNull
    public MessageExchangeHelper getMessageExchangeHelper() {
        return mMessageExchangeHelper;
    }

    @NonNull
    public SettingsManager getSettingsManager() {
        return mSettingsManager;
    }

    @NonNull
    public MusicLibraryCache getCache() {
        return mMusicLibraryCache;
    }

    @NonNull
    public MusicLibraryNavigator getMusicLibraryNavigator() {
        return mMusicLibraryNavigator;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        CrashDeliveryService.launch(throwable, this);
        mDefaultExceptionHandler.uncaughtException(thread, throwable);
    }
}
