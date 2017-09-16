package com.pdyjak.powerampwear

import android.app.Fragment
import android.content.Context
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
internal val Context.ambientModeStateProviderInternal: AmbientModeStateProviderImpl
    get() =  ambientModeStateProvider as AmbientModeStateProviderImpl
