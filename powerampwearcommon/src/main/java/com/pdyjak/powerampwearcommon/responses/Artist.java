package com.pdyjak.powerampwearcommon.responses;

import android.support.annotation.NonNull;

public class Artist {
    @NonNull
    public final String id;
    @NonNull
    public final String name;
    public final int songsCount;

    public Artist(@NonNull String id, @NonNull String name, int songsCount) {
        this.id = id;
        this.name = name;
        this.songsCount = songsCount;
    }
}
