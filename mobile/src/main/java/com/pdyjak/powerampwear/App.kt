package com.pdyjak.powerampwear

import android.app.Application

import com.google.firebase.FirebaseApp

class App : Application() {

    internal lateinit var settingsManager: SettingsManager

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.ENABLE_CRASH_REPORTING) FirebaseApp.initializeApp(this)
        settingsManager = SettingsManager(this)
    }
}
