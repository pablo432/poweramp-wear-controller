package com.pdyjak.powerampwear;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;
import com.pdyjak.powerampwear.music_browser.albums.AlbumItem;
import com.pdyjak.powerampwear.music_browser.artists.ArtistItem;
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem;
import com.pdyjak.powerampwear.music_browser.files.FileItem;
import com.pdyjak.powerampwear.music_browser.folders.FolderItem;

class MusicBrowserListenerAdapter implements MusicLibraryNavigator.Listener {
    @Override
    public void onCategorySelected(@NonNull CategoryItem item, boolean fromPlayer) {
    }

    @Override
    public void onFolderSelected(@NonNull FolderItem item, boolean fromPlayer,
                                 @Nullable String scrollTo) {
    }

    @Override
    public void onAlbumSelected(@NonNull AlbumItem item, boolean fromPlayer) {
    }

    @Override
    public void onArtistSelected(@NonNull ArtistItem item, boolean fromPlayer) {
    }

    @Override
    public void onFileSelected(@NonNull FileItem item, boolean fromPlayer) {
    }
}
