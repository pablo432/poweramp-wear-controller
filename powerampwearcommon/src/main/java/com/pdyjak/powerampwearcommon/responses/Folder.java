package com.pdyjak.powerampwearcommon.responses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Folder {
    @NonNull
    public final String id;
    @Nullable
    public final String name;
    @Nullable
    public final String parentName;

    public Folder(@NonNull String id, @Nullable String name, @Nullable String parentName) {
        this.id = id;
        this.name = name;
        this.parentName = parentName;
    }
}
