package com.pdyjak.powerampwear.settings;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

public class SettingsManager {
    private static final String CIRCULAR_SCROLLING_KEY = "use_circular_scrolling";
    private static final String SHOW_CLOCK_KEY = "show_clock";
    private static final String VOLUME_CONTROLS_ONBOARDING_KEY = "vc_onboarding";
    private static final String QUICK_NAVIGATION_HINT_KEY = "quick_nav";

    public static class Listener {
        public void onCircularScrollingChanged() {}
        public void onClockSettingChanged() {}
    }

    @NonNull
    private final Set<Listener> mSettingsListeners = new HashSet<>();
    @NonNull
    private final SharedPreferences mPrefs;

    public SettingsManager(@NonNull SharedPreferences sharedPreferences) {
        mPrefs = sharedPreferences;
    }

    public boolean quickNavigationHintShown() {
        return mPrefs.getBoolean(QUICK_NAVIGATION_HINT_KEY, false);
    }

    public void saveQuickNavigationHintShown() {
        mPrefs.edit().putBoolean(QUICK_NAVIGATION_HINT_KEY, true).apply();
    }

    public boolean volumeControlsOnboardingShown() {
        return mPrefs.getBoolean(VOLUME_CONTROLS_ONBOARDING_KEY, false);
    }

    public void saveVolumeControlsOnboardingShown() {
        mPrefs.edit().putBoolean(VOLUME_CONTROLS_ONBOARDING_KEY, true).apply();
    }

    public boolean useCircularScrollingGesture() {
        return mPrefs.getBoolean(CIRCULAR_SCROLLING_KEY, false);
    }

    public boolean shouldShowClock() {
        return mPrefs.getBoolean(SHOW_CLOCK_KEY, true);
    }

    public void saveUseCircularScrolling(boolean enabled) {
        if (enabled == useCircularScrollingGesture()) return;
        mPrefs.edit().putBoolean(CIRCULAR_SCROLLING_KEY, enabled).apply();
        Set<Listener> copy = new HashSet<>(mSettingsListeners);
        for (Listener listener : copy) {
            listener.onCircularScrollingChanged();
        }
    }

    public void saveShowClock(boolean enabled) {
        if (enabled == shouldShowClock()) return;
        mPrefs.edit().putBoolean(SHOW_CLOCK_KEY, enabled).apply();
        Set<Listener> copy = new HashSet<>(mSettingsListeners);
        for (Listener listener : copy) {
            listener.onClockSettingChanged();
        }
    }

    public void addSettingsListener(@NonNull Listener listener) {
        mSettingsListeners.add(listener);
    }

    public void removeListener(@NonNull Listener listener) {
        mSettingsListeners.remove(listener);
    }
}
