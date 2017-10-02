package com.pdyjak.powerampwear.common

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.pdyjak.powerampwear.App
import com.pdyjak.powerampwear.MessageExchangeHelper
import com.pdyjak.powerampwear.music_browser.MusicLibraryCache
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator
import com.pdyjak.powerampwear.player.AmbientModeStateProvider
import com.pdyjak.powerampwear.settings.SettingsManager

val Context.app: App get() = applicationContext as App
val Context.settingsManager: SettingsManager get() = app.settingsManager
val Context.musicLibraryCache: MusicLibraryCache get() = app.cache
val Context.musicLibraryNavigator: MusicLibraryNavigator get() = app.musicLibraryNavigator
val Context.messageExchangeHelper: MessageExchangeHelper get() = app.messageExchangeHelper
val Context.ambientModeStateProvider: AmbientModeStateProvider get() = app.ambientModeStateProvider

fun String.nullIfEmpty() = if (TextUtils.isEmpty(this)) null else this

@Suppress("UNCHECKED_CAST")
infix fun <T> View.byId(viewId: Int): T {
    return findViewById(viewId) as T
}
