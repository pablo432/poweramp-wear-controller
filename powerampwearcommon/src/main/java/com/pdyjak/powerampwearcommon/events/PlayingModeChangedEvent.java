package com.pdyjak.powerampwearcommon.events;

import android.support.annotation.NonNull;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class PlayingModeChangedEvent implements Message {
    public static final String PATH = "/playing_mode_changed";

    public final int shuffleMode;
    public final int repeatMode;

    public PlayingModeChangedEvent(int shuffleMode, int repeatMode) {
        this.shuffleMode = shuffleMode;
        this.repeatMode = repeatMode;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    @Override
    public String getPath() {
        return PATH;
    }

    public static PlayingModeChangedEvent fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, PlayingModeChangedEvent.class);
    }
}
