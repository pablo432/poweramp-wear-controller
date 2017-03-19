package com.pdyjak.powerampwear.music_browser.artists;

import android.support.annotation.NonNull;

import com.pdyjak.powerampwear.music_browser.Clickable;
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;

public class ArtistItem implements Clickable {
    @NonNull
    private final MusicLibraryNavigator mMusicLibraryNavigator;

    @NonNull
    public final String id;
    @NonNull
    final String name;
    final int songsCount;

    ArtistItem(@NonNull MusicLibraryNavigator navigator,
            @NonNull String id, @NonNull String name, int songsCount) {
        mMusicLibraryNavigator = navigator;
        this.id = id;
        this.name = name;
        this.songsCount = songsCount;
    }

    @Override
    public void onClicked() {
        mMusicLibraryNavigator.selectArtist(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArtistItem item = (ArtistItem) o;
        if (songsCount != item.songsCount) return false;
        if (!mMusicLibraryNavigator.equals(item.mMusicLibraryNavigator)) return false;
        if (!id.equals(item.id)) return false;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        int result = mMusicLibraryNavigator.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + songsCount;
        return result;
    }
}
