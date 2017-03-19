package com.pdyjak.powerampwearcommon.responses;

import android.support.annotation.NonNull;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

import java.util.ArrayList;
import java.util.List;

public class FoldersListResponse implements Message {
    public static final String PATH = "/folders_response";

    @NonNull
    private final List<Folder> mFoldersList;

    public FoldersListResponse(@NonNull List<Folder> folders) {
        mFoldersList = folders;
    }

    public List<Folder> getFoldersList() {
        return new ArrayList<>(mFoldersList);
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    @Override
    public String getPath() {
        return PATH;
    }

    public static FoldersListResponse fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, FoldersListResponse.class);
    }
}
