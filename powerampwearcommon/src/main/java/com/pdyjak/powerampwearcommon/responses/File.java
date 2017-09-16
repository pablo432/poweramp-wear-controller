package com.pdyjak.powerampwearcommon.responses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class File {
    @Nullable
    public String contextualId; // For queues, maybe playlists, too?
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
