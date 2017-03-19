package com.pdyjak.powerampwearcommon.events;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class RepeatModeChangedEvent implements Message {
    public static final String PATH = "/repeat_changed";

    public final int mode;

    public RepeatModeChangedEvent(int mode) {
        this.mode = mode;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    @Override
    public String getPath() {
        return PATH;
    }

    public static RepeatModeChangedEvent fromBytes(byte[] bytes) {
        return BytesHelper.fromBytes(bytes, RepeatModeChangedEvent.class);
    }
}
