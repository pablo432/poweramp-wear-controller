package com.pdyjak.powerampwear.music_browser.artists;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwear.music_browser.Clickable;
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;

public class ArtistItem implements Clickable {
    @NonNull
    private final MusicLibraryNavigator mMusicLibraryNavigator;

    @NonNull
    public final String id;
    @Nullable
    final String name;
    final int songsCount;

    ArtistItem(@NonNull MusicLibraryNavigator navigator,
            @NonNull String id, @Nullable String name, int songsCount) {
        mMusicLibraryNavigator = navigator;
        this.id = id;
        this.name = name;
        this.songsCount = songsCount;
    }

    @Override
    public void onClicked() {
        mMusicLibraryNavigator.selectArtist(this, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArtistItem that = (ArtistItem) o;

        if (songsCount != that.songsCount) return false;
        if (!id.equals(that.id)) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + songsCount;
        return result;
    }
}
