package com.pdyjak.powerampwearcommon.requests;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.responses.Parent;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class GetFilesRequest implements Message {
    public static final String PATH = "/get_files";

    @Nullable
    public final Parent parent;

    public GetFilesRequest(@Nullable Parent parent) {
        this.parent = parent;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    @Override
    public String getPath() {
        return PATH;
    }

    public static GetFilesRequest fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, GetFilesRequest.class);
    }
}