package com.pdyjak.powerampwearcommon.events;

import android.support.annotation.NonNull;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class TrackPositionSyncEvent implements Message {
    public static final String PATH = "/track_pos_sync";

    public final int position;

    public TrackPositionSyncEvent(int pos) {
        position = pos;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    @Override
    public String getPath() {
        return PATH;
    }

    public static TrackPositionSyncEvent fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, TrackPositionSyncEvent.class);
    }
}
