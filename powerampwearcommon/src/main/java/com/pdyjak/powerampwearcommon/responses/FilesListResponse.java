package com.pdyjak.powerampwearcommon.responses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.utils.BytesHelper;

import java.util.ArrayList;
import java.util.List;

public class FilesListResponse implements Message {
    public static String PATH = "/files_response";

    @Nullable
    public final Parent parent;
    @NonNull
    private final List<File> mFilesList;

    public FilesListResponse(@Nullable Parent parent, @NonNull List<File> files) {
        this.parent = parent;
        mFilesList = files;
    }

    public List<File> getFilesList() {
        return new ArrayList<>(mFilesList);
    }

    @Override
    public byte[] toBytes() {
        return BytesHelper.toBytes(this);
    }

    @Override
    public String getPath() {
        return PATH;
    }

    public static FilesListResponse fromBytes(@NonNull byte[] bytes) {
        return BytesHelper.fromBytes(bytes, FilesListResponse.class);
    }
}
