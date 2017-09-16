package com.pdyjak.powerampwear

import android.content.Context
import android.content.pm.PackageManager
import com.pdyjak.powerampwearcommon.events.TrackChangedEvent

val Context.app: App get() = applicationContext as App
val Context.settingsManager: SettingsManager get() = app.settingsManager

fun PackageManager.isApplicationInstalled(packageName: String): Boolean {
    try {
        getPackageInfo(packageName, 0)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
        return false
    }
}

fun TrackChangedEvent.artistAlbumEquals(event: TrackChangedEvent?): Boolean {
    return album == event?.album && artist == event?.artist
}