package com.pdyjak.powerampwear

import android.app.Application
import android.content.Context

import com.pdyjak.powerampwear.music_browser.MusicLibraryCache
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator
import com.pdyjak.powerampwear.player.AmbientModeStateProvider
import com.pdyjak.powerampwear.settings.SettingsManager

class App : Application(), Thread.UncaughtExceptionHandler {

    private lateinit var mDefaultExceptionHandler: Thread.UncaughtExceptionHandler
    internal lateinit var settingsManager: SettingsManager private set
    internal lateinit var cache: MusicLibraryCache private set
    internal lateinit var musicLibraryNavigator: MusicLibraryNavigator private set
    internal lateinit var messageExchangeHelper: MessageExchangeHelper private set
    internal lateinit var ambientModeStateProvider: AmbientModeStateProvider private set

    override fun onCreate() {
        super.onCreate()
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        settingsManager = SettingsManager(
                getSharedPreferences(getString(R.string.prefs_file_key), Context.MODE_PRIVATE))
        cache = MusicLibraryCacheImpl()
        musicLibraryNavigator = MusicLibraryNavigatorImpl()
        messageExchangeHelper = MessageExchangeHelper(this)
        ambientModeStateProvider = AmbientModeStateProviderImpl()
    }

    override fun onTerminate() {
        super.onTerminate()
        messageExchangeHelper.dispose()
    }

    fun onResume() {
        // Called by MainActivity - it's good as long as this is one-activity application
        messageExchangeHelper.onResume()
    }

    fun onPause() {
        // Called by MainActivity - it's good as long as this is one-activity application
        messageExchangeHelper.onPause()
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        CrashDeliveryService.launch(throwable, this)
        mDefaultExceptionHandler.uncaughtException(thread, throwable)
    }
}
