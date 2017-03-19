package com.pdyjak.powerampwear.music_browser.albums;

import android.support.annotation.NonNull;

import com.pdyjak.powerampwear.music_browser.Clickable;
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;

public class AlbumItem implements Clickable {
    @NonNull
    private final MusicLibraryNavigator mMusicLibraryNavigator;

    @NonNull
    public final String id;
    @NonNull
    final String name;
    @NonNull
    final String artist;

    AlbumItem(@NonNull MusicLibraryNavigator helper, @NonNull String id, @NonNull String name,
            @NonNull String artist) {
        mMusicLibraryNavigator = helper;
        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    @Override
    public void onClicked() {
        mMusicLibraryNavigator.selectAlbum(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlbumItem albumItem = (AlbumItem) o;
        if (!mMusicLibraryNavigator.equals(albumItem.mMusicLibraryNavigator)) return false;
        if (!id.equals(albumItem.id)) return false;
        if (!name.equals(albumItem.name)) return false;
        return artist.equals(albumItem.artist);
    }

    @Override
    public int hashCode() {
        int result = mMusicLibraryNavigator.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + artist.hashCode();
        return result;
    }
}
