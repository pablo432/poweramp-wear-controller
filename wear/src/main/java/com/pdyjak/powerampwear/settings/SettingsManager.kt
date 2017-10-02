package com.pdyjak.powerampwear.settings

import android.content.SharedPreferences
import com.pdyjak.powerampwear.common.SimpleEvent

class SettingsManager(private val mPrefs: SharedPreferences) {

    companion object {
        private val CIRCULAR_SCROLLING_KEY = "use_circular_scrolling"
        private val SHOW_CLOCK_KEY = "show_clock"
        private val VOLUME_CONTROLS_ONBOARDING_KEY = "vc_onboarding"
        private val QUICK_NAVIGATION_HINT_KEY = "quick_nav"
    }

    val onCircularScrollingChanged = SimpleEvent()
    val onClockSettingChanged = SimpleEvent()

    fun quickNavigationHintShown(): Boolean {
        return mPrefs.getBoolean(QUICK_NAVIGATION_HINT_KEY, false)
    }

    fun saveQuickNavigationHintShown() {
        mPrefs.edit().putBoolean(QUICK_NAVIGATION_HINT_KEY, true).apply()
    }

    fun volumeControlsOnboardingShown(): Boolean {
        return mPrefs.getBoolean(VOLUME_CONTROLS_ONBOARDING_KEY, false)
    }

    fun saveVolumeControlsOnboardingShown() {
        mPrefs.edit().putBoolean(VOLUME_CONTROLS_ONBOARDING_KEY, true).apply()
    }

    fun useCircularScrollingGesture(): Boolean {
        return mPrefs.getBoolean(CIRCULAR_SCROLLING_KEY, false)
    }

    fun shouldShowClock(): Boolean {
        return mPrefs.getBoolean(SHOW_CLOCK_KEY, true)
    }

    fun saveUseCircularScrolling(enabled: Boolean) {
        if (enabled == useCircularScrollingGesture()) return
        mPrefs.edit().putBoolean(CIRCULAR_SCROLLING_KEY, enabled).apply()
        onCircularScrollingChanged()
    }

    fun saveShowClock(enabled: Boolean) {
        if (enabled == shouldShowClock()) return
        mPrefs.edit().putBoolean(SHOW_CLOCK_KEY, enabled).apply()
        onClockSettingChanged()
    }
}
