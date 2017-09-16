package com.pdyjak.powerampwearcommon.requests;

import android.support.annotation.NonNull;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

public class FindParentRequest implements Message {
    public static final String PATH = "/find_parent";

    public static final int PARENT_FOLDER = 0;
    public static final int PARENT_ALBUM = 1;
    public static final int PARENT_ARTIST = 2;

    public final int parent;

    public FindParentRequest(int parent) {
        this.parent = parent;
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    public static FindParentRequest fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, FindParentRequest.class);
    }
}
