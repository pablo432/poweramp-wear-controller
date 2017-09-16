package com.pdyjak.powerampwear.music_browser.albums;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwear.music_browser.Clickable;
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;

public class AlbumItem implements Clickable {
    @NonNull
    private final MusicLibraryNavigator mMusicLibraryNavigator;

    @NonNull
    public final String id;
    @Nullable
    final String name;
    @Nullable
    final String artist;

    public AlbumItem(@NonNull MusicLibraryNavigator helper, @NonNull String id, @Nullable String name,
            @Nullable String artist) {
        mMusicLibraryNavigator = helper;
        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    @Override
    public void onClicked() {
        mMusicLibraryNavigator.selectAlbum(this, false, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlbumItem albumItem = (AlbumItem) o;

        if (!id.equals(albumItem.id)) return false;
        if (name != null ? !name.equals(albumItem.name) : albumItem.name != null) return false;
        return artist != null ? artist.equals(albumItem.artist) : albumItem.artist == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        return result;
    }
}
