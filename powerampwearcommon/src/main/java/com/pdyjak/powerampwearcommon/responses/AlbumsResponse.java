package com.pdyjak.powerampwearcommon.responses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

import java.util.ArrayList;
import java.util.List;

public class AlbumsResponse implements Message {
    public static final String PATH = "/albums_response";

    @Nullable
    public final Parent parent;
    @NonNull
    private final List<Album> mAlbumsList;

    public AlbumsResponse(@Nullable Parent parent, @NonNull List<Album> albumsList) {
        this.parent = parent;
        mAlbumsList = albumsList;
    }

    @NonNull
    public List<Album> getAlbums() {
        return new ArrayList<>(mAlbumsList);
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    public static AlbumsResponse fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, AlbumsResponse.class);
    }
}
