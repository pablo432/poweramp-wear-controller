package com.pdyjak.powerampwear.player;

import android.support.annotation.NonNull;

public interface AmbientModeStateProvider {

    interface Listener {
        void onAmbientModeStateChanged();
    }

    boolean isInAmbientMode();
    void addAmbientModeListener(@NonNull Listener listener);
    void removeAmbientModeListener(@NonNull Listener listener);
}
