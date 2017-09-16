package com.pdyjak.powerampwearcommon.events;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class PositionChangedEvent implements Message {
    public static final String PATH = "/position_changed";

    public final int position;
    public final int duration;

    public PositionChangedEvent(int position, int duration) {
        this.position = position;
        this.duration = duration;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    public static PositionChangedEvent fromBytes(byte[] bytes) {
        return BytesHelper.fromBytes(bytes, PositionChangedEvent.class);
    }
}
