package com.pdyjak.powerampwear;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;
import com.pdyjak.powerampwear.music_browser.albums.AlbumItem;
import com.pdyjak.powerampwear.music_browser.artists.ArtistItem;
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem;
import com.pdyjak.powerampwear.music_browser.files.FileItem;
import com.pdyjak.powerampwear.music_browser.folders.FolderItem;

import java.util.HashSet;
import java.util.Set;

public class MusicLibraryNavigatorImpl implements MusicLibraryNavigator {

    @NonNull
    private final Set<MusicLibraryNavigator.Listener> mMusicBrowserListeners = new HashSet<>();

    @Override
    public void selectCategory(@NonNull CategoryItem item, boolean fromPlayer) {
        Set<Listener> copy = new HashSet<>(mMusicBrowserListeners);
        for (MusicLibraryNavigator.Listener listener : copy) {
            listener.onCategorySelected(item, fromPlayer);
        }
    }

    @Override
    public void selectFolder(@NonNull FolderItem item, boolean fromPlayer,
                             @Nullable String scrollTo) {
        Set<MusicLibraryNavigator.Listener> copy = new HashSet<>(mMusicBrowserListeners);
        for (MusicLibraryNavigator.Listener listener : copy) {
            listener.onFolderSelected(item, fromPlayer, scrollTo);
        }
    }

    @Override
    public void selectAlbum(@NonNull AlbumItem item, boolean fromPlayer) {
        Set<MusicLibraryNavigator.Listener> copy = new HashSet<>(mMusicBrowserListeners);
        for (MusicLibraryNavigator.Listener listener : copy) {
            listener.onAlbumSelected(item, fromPlayer);
        }
    }

    @Override
    public void selectFile(@NonNull FileItem item, boolean fromPlayer) {
        Set<MusicLibraryNavigator.Listener> copy = new HashSet<>(mMusicBrowserListeners);
        for (MusicLibraryNavigator.Listener listener : copy) {
            listener.onFileSelected(item, fromPlayer);
        }
    }

    @Override
    public void selectArtist(@NonNull ArtistItem item, boolean fromPlayer) {
        Set<MusicLibraryNavigator.Listener> copy = new HashSet<>(mMusicBrowserListeners);
        for (MusicLibraryNavigator.Listener listener : copy) {
            listener.onArtistSelected(item, fromPlayer);
        }
    }

    @Override
    public void addLibraryNavigationListener(@NonNull Listener listener) {
        mMusicBrowserListeners.add(listener);
    }

    @Override
    public void removeLibraryNavigationListener(@NonNull Listener listener) {
        mMusicBrowserListeners.remove(listener);
    }
}
