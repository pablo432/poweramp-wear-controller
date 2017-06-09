package com.pdyjak.powerampwear.settings;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

public class SettingsManager {
    private static final String CIRCULAR_SCROLLING_KEY = "use_circular_scrolling";
    private static final String VOLUME_CONTROLS_ONBOARDING_KEY = "vc_onboarding";

    public static class Listener {
        public void onCircularScrollingChanged() {}
    }

    @NonNull
    private final Set<Listener> mSettingsListeners = new HashSet<>();
    @NonNull
    private final SharedPreferences mPrefs;

    public SettingsManager(@NonNull SharedPreferences sharedPreferences) {
        mPrefs = sharedPreferences;
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

    public void saveUseCircularScrolling(boolean value) {
        if (value == useCircularScrollingGesture()) return;
        mPrefs.edit().putBoolean(CIRCULAR_SCROLLING_KEY, value).apply();
        Set<Listener> copy = new HashSet<>(mSettingsListeners);
        for (Listener listener : copy) {
            listener.onCircularScrollingChanged();
        }
    }

    public void addSettingsListener(@NonNull Listener listener) {
        mSettingsListeners.add(listener);
    }

    public void removeListener(@NonNull Listener listener) {
        mSettingsListeners.remove(listener);
    }
}
