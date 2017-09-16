package com.pdyjak.powerampwearcommon.events;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class ShuffleModeChangedEvent implements Message {
    public static final String PATH = "/shuffle_changed";

    public final int mode;

    public ShuffleModeChangedEvent(int mode) {
        this.mode = mode;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    public static ShuffleModeChangedEvent fromBytes(byte[] bytes) {
        return BytesHelper.fromBytes(bytes, ShuffleModeChangedEvent.class);
    }
}
