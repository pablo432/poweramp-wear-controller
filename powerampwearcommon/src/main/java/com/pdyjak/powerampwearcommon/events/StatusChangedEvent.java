package com.pdyjak.powerampwearcommon.events;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class StatusChangedEvent implements Message {
    public static final String PATH = "/status_changed";

    public final int status;
    public final boolean paused;

    public StatusChangedEvent(int status, boolean paused) {
        this.status = status;
        this.paused = paused;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    public static StatusChangedEvent fromBytes(byte[] bytes) {
        return BytesHelper.fromBytes(bytes, StatusChangedEvent.class);
    }
}
