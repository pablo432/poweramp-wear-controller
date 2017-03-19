package com.pdyjak.powerampwearcommon.responses;

import android.support.annotation.NonNull;

public class Album {
    @NonNull
    public final String id;
    @NonNull
    public final String name;
    @NonNull
    public final String artistName;

    public Album(@NonNull String id, @NonNull String name, @NonNull String artistName) {
        this.id = id;
        this.name = name;
        this.artistName = artistName;
    }
}
