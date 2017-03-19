package com.pdyjak.powerampwearcommon.responses;

import android.support.annotation.NonNull;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

import java.util.ArrayList;
import java.util.List;

public class ArtistsResponse implements Message {
    public static final String PATH = "/artists_response";

    @NonNull
    private final List<Artist> mArtistsList;

    public ArtistsResponse(@NonNull List<Artist> artistsList) {
        mArtistsList = artistsList;
    }

    @NonNull
    public List<Artist> getArtists() {
        return new ArrayList<>(mArtistsList);
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    @Override
    public String getPath() {
        return PATH;
    }

    public static ArtistsResponse fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, ArtistsResponse.class);
    }
}
