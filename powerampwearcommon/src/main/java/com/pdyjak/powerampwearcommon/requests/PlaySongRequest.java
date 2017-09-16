package com.pdyjak.powerampwearcommon.requests;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.responses.Parent;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class PlaySongRequest implements Message {
    public static final String PATH = "/play_song";

    @NonNull
    public final String trackId;
    @Nullable
    public final Parent parent;

    public PlaySongRequest(@NonNull String trackId, @Nullable Parent parent) {
        this.trackId = trackId;
        this.parent = parent;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    public static PlaySongRequest fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, PlaySongRequest.class);
    }
}
