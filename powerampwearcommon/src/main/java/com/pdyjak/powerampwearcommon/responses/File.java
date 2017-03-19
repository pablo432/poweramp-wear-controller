package com.pdyjak.powerampwearcommon.responses;

import android.support.annotation.NonNull;

public class File {
    @NonNull
    public final String id;
    @NonNull
    public final String title;
    @NonNull
    public final String artist;
    @NonNull
    public final String album;
    public final long duration;

    public File(@NonNull String id, @NonNull String title, @NonNull String artist,
            @NonNull String album, long duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }
}
