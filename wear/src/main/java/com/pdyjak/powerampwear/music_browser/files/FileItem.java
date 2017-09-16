package com.pdyjak.powerampwear.music_browser.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwear.music_browser.Clickable;
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;
import com.pdyjak.powerampwearcommon.responses.Parent;

public class FileItem implements Clickable {
    @NonNull
    private final MusicLibraryNavigator mMusicLibraryNavigator;

    @Nullable
    public final Parent parent;
    @NonNull
    public final String trackId;
    @Nullable
    final String title;
    @Nullable
    final String artist;
    @Nullable
    final String album;
    final long duration;

    FileItem(@NonNull MusicLibraryNavigator helper, @Nullable Parent parent,
            @NonNull String trackId, @Nullable String title, @Nullable String artist,
            @Nullable String album, long duration) {
        mMusicLibraryNavigator = helper;
        this.parent = parent;
        this.trackId = trackId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    @Override
    public void onClicked() {
        mMusicLibraryNavigator.selectFile(this, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileItem fileItem = (FileItem) o;

        if (duration != fileItem.duration) return false;
        if (parent != null ? !parent.equals(fileItem.parent) : fileItem.parent != null)
            return false;
        if (!trackId.equals(fileItem.trackId)) return false;
        if (title != null ? !title.equals(fileItem.title) : fileItem.title != null) return false;
        if (artist != null ? !artist.equals(fileItem.artist) : fileItem.artist != null)
            return false;
        return album != null ? album.equals(fileItem.album) : fileItem.album == null;

    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + trackId.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + (album != null ? album.hashCode() : 0);
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        return result;
    }
}
