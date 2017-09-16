package com.pdyjak.powerampwearcommon.events;

import com.pdyjak.powerampwearcommon.Message;

public class AlbumArtChangedEvent implements Message {
    public static String PATH = "/album_art_changed";

    private final byte[] mBytes;

    public AlbumArtChangedEvent(byte[] bytes) {
        mBytes = bytes;
    }

    @Override
    public byte[] toBytes() {
        return mBytes;
    }
}
