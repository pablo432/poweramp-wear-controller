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
    @NonNull
    final String title;
    @NonNull
    final String artist;
    @NonNull
    final String album;
    final long duration;

    FileItem(@NonNull MusicLibraryNavigator helper, @Nullable Parent parent, @NonNull String trackId,
            @NonNull String title, @NonNull String artist, @NonNull String album, long duration) {
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
        mMusicLibraryNavigator.selectFile(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileItem fileItem = (FileItem) o;

        if (duration != fileItem.duration) return false;
        if (!mMusicLibraryNavigator.equals(fileItem.mMusicLibraryNavigator)) return false;
        if (parent != null ? !parent.equals(fileItem.parent) : fileItem.parent != null)
            return false;
        if (!trackId.equals(fileItem.trackId)) return false;
        if (!title.equals(fileItem.title)) return false;
        if (!artist.equals(fileItem.artist)) return false;
        return album.equals(fileItem.album);

    }

    @Override
    public int hashCode() {
        int result = mMusicLibraryNavigator.hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + trackId.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + artist.hashCode();
        result = 31 * result + album.hashCode();
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        return result;
    }
}
