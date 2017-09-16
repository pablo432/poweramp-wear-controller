package com.pdyjak.powerampwear

import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator
import com.pdyjak.powerampwear.music_browser.albums.AlbumItem
import com.pdyjak.powerampwear.music_browser.artists.ArtistItem
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem
import com.pdyjak.powerampwear.music_browser.files.FileItem
import com.pdyjak.powerampwear.music_browser.folders.FolderItem

internal open class MusicBrowserListenerAdapter : MusicLibraryNavigator.Listener {
    override fun onCategorySelected(item: CategoryItem, fromPlayer: Boolean, scrollTo: String?) {}
    override fun onFolderSelected(item: FolderItem, fromPlayer: Boolean, scrollTo: String?) {}
    override fun onAlbumSelected(item: AlbumItem, fromPlayer: Boolean, scrollTo: String?) {}
    override fun onArtistSelected(item: ArtistItem, fromPlayer: Boolean) {}
    override fun onFileSelected(item: FileItem, fromPlayer: Boolean) {}
}
