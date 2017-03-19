package com.pdyjak.powerampwearcommon.events;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class TrackChangedEvent implements Message {
    public static final String PATH = "/track_changed";

    public final String title;
    public final String artist;
    public final String album;
    public final int initialPosition;
    public final int duration;
    public final int sampleRate;
    public final int bitRate;
    public final String codec;

    public TrackChangedEvent(String title, String artist, String album, int initialPosition,
            int duration, int sampleRate, int bitRate, String codec) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.initialPosition = initialPosition;
        this.duration = duration;
        this.sampleRate = sampleRate;
        this.bitRate = bitRate;
        this.codec = codec;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    @Override
    public String getPath() {
        return PATH;
    }

    public static TrackChangedEvent fromBytes(byte[] bytes) {
        return BytesHelper.fromBytes(bytes, TrackChangedEvent.class);
    }
}
