package com.pdyjak.powerampwear

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    companion object {
        private const val ONBOARDING_COMPLETED_KEY = "onboarding_completed"
        private const val SHOW_ALBUM_ART_KEY = "show_albumart"
        private const val WAKELOCK_ON_SONG_CHANGE = "albumart_wakelock"
    }

    private val mPrefs: SharedPreferences

    init {
        val preferencesFileName = context.getString(R.string.prefs_key)
        mPrefs = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE)
    }

    var onboardingCompleted
        get() = mPrefs.getBoolean(ONBOARDING_COMPLETED_KEY, false)
        set(value) { mPrefs.edit().putBoolean(ONBOARDING_COMPLETED_KEY, value).apply() }

    var shouldShowAlbumArt
        get() = mPrefs.getBoolean(SHOW_ALBUM_ART_KEY, true)
        set(value) { mPrefs.edit().putBoolean(SHOW_ALBUM_ART_KEY, value).apply() }

    var shouldWakeWhenChangingSongs
        get() = mPrefs.getBoolean(WAKELOCK_ON_SONG_CHANGE, shouldShowAlbumArt)
        set(value) { mPrefs.edit().putBoolean(WAKELOCK_ON_SONG_CHANGE, value).apply() }
}