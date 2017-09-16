package com.pdyjak.powerampwear

import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator
import com.pdyjak.powerampwear.music_browser.albums.AlbumItem
import com.pdyjak.powerampwear.music_browser.artists.ArtistItem
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem
import com.pdyjak.powerampwear.music_browser.files.FileItem
import com.pdyjak.powerampwear.music_browser.folders.FolderItem

import java.util.HashSet

class MusicLibraryNavigatorImpl : MusicLibraryNavigator {

    private val mMusicBrowserListeners = HashSet<MusicLibraryNavigator.Listener>()

    override fun selectCategory(item: CategoryItem, fromPlayer: Boolean, scrollTo: String?) {
        val copy = HashSet(mMusicBrowserListeners)
        for (listener in copy) listener.onCategorySelected(item, fromPlayer, scrollTo)
    }

    override fun selectFolder(item: FolderItem, fromPlayer: Boolean, scrollTo: String?) {
        val copy = HashSet(mMusicBrowserListeners)
        for (listener in copy) listener.onFolderSelected(item, fromPlayer, scrollTo)
    }

    override fun selectAlbum(item: AlbumItem, fromPlayer: Boolean, scrollTo: String?) {
        val copy = HashSet(mMusicBrowserListeners)
        for (listener in copy) listener.onAlbumSelected(item, fromPlayer, scrollTo)
    }

    override fun selectFile(item: FileItem, fromPlayer: Boolean) {
        val copy = HashSet(mMusicBrowserListeners)
        for (listener in copy) listener.onFileSelected(item, fromPlayer)
    }

    override fun selectArtist(item: ArtistItem, fromPlayer: Boolean) {
        val copy = HashSet(mMusicBrowserListeners)
        for (listener in copy) listener.onArtistSelected(item, fromPlayer)
    }

    override fun addLibraryNavigationListener(listener: MusicLibraryNavigator.Listener) {
        mMusicBrowserListeners.add(listener)
    }

    override fun removeLibraryNavigationListener(listener: MusicLibraryNavigator.Listener) {
        mMusicBrowserListeners.remove(listener)
    }
}
