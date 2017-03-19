package com.pdyjak.powerampwear;

import android.support.annotation.NonNull;

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
    public void selectCategory(@NonNull CategoryItem item) {
        Set<Listener> copy = new HashSet<>(mMusicBrowserListeners);
        for (MusicLibraryNavigator.Listener listener : copy) {
            listener.onCategorySelected(item);
        }
    }

    @Override
    public void selectFolder(@NonNull FolderItem item) {
        Set<MusicLibraryNavigator.Listener> copy = new HashSet<>(mMusicBrowserListeners);
        for (MusicLibraryNavigator.Listener listener : copy) {
            listener.onFolderSelected(item);
        }
    }

    @Override
    public void selectAlbum(@NonNull AlbumItem item) {
        Set<MusicLibraryNavigator.Listener> copy = new HashSet<>(mMusicBrowserListeners);
        for (MusicLibraryNavigator.Listener listener : copy) {
            listener.onAlbumSelected(item);
        }
    }

    @Override
    public void selectFile(@NonNull FileItem item) {
        Set<MusicLibraryNavigator.Listener> copy = new HashSet<>(mMusicBrowserListeners);
        for (MusicLibraryNavigator.Listener listener : copy) {
            listener.onFileSelected(item);
        }
    }

    @Override
    public void selectArtist(@NonNull ArtistItem item) {
        Set<MusicLibraryNavigator.Listener> copy = new HashSet<>(mMusicBrowserListeners);
        for (MusicLibraryNavigator.Listener listener : copy) {
            listener.onArtistSelected(item);
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
