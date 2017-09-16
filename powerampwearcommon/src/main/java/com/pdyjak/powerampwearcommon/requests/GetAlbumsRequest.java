package com.pdyjak.powerampwearcommon.requests;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.responses.Parent;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class GetAlbumsRequest implements Message {
    public static final String PATH = "/get_albums";

    @Nullable
    public final Parent parent;

    public GetAlbumsRequest(@Nullable Parent parent) {
        this.parent = parent;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    public static GetAlbumsRequest fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, GetAlbumsRequest.class);
    }
}
