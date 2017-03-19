package com.pdyjak.powerampwear.music_browser;

import android.support.annotation.NonNull;

import com.pdyjak.powerampwear.music_browser.albums.AlbumItem;
import com.pdyjak.powerampwear.music_browser.artists.ArtistItem;
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem;
import com.pdyjak.powerampwear.music_browser.files.FileItem;
import com.pdyjak.powerampwear.music_browser.folders.FolderItem;

/**
 * Few words of explanation for this weird pattern (this interface is implemented by App class):
 * Music library browser is based on FragmentTransactions inside LibraryExplorerFragment - so to
 * replace fragments, LibraryExplorerFragment needs to know which item has been clicked inside each
 * fragment (like CategorySelectionFragment, FoldersBrowserFragment, FilesBrowserFragment) - the
 * easiest solution to know it would be just to inject/set some listener on each of these fragments
 * after instantiating them. BUT Fragments need to have a default constructor, to allow Android to
 * kill and re-create fragments (or do whatever magic it does with fragments) - in that case we
 * would end up with no listener set on a fragment. Having App class always around (via Context)
 * allows to solve this problem  - Fragments extract MusicLibraryNavigator and call whatever methods
 * they need to, then implementation delivers these events to all listeners.
 */
public interface MusicLibraryNavigator {
    interface Listener {
        void onCategorySelected(@NonNull CategoryItem item);
        void onFolderSelected(@NonNull FolderItem item);
        void onAlbumSelected(@NonNull AlbumItem item);
        void onArtistSelected(@NonNull ArtistItem item);
        void onFileSelected(@NonNull FileItem item);
    }

    void selectCategory(@NonNull CategoryItem item);
    void selectFolder(@NonNull FolderItem item);
    void selectAlbum(@NonNull AlbumItem item);
    void selectFile(@NonNull FileItem item);
    void selectArtist(@NonNull ArtistItem item);
    void addLibraryNavigationListener(@NonNull Listener listener);
    void removeLibraryNavigationListener(@NonNull Listener listener);
}
