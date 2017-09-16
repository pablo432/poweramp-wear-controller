package com.pdyjak.powerampwearcommon.responses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class FindParentResponse implements Message {
    public static final String PATH = "/find_parent_response";

    @NonNull
    public final Parent parent;
    @Nullable
    public final String title;

    public FindParentResponse(@NonNull Parent parent, @Nullable String title) {
        this.parent = parent;
        this.title = title;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    public static FindParentResponse fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, FindParentResponse.class);
    }
}
