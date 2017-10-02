package com.pdyjak.powerampwear

import com.pdyjak.powerampwear.common.Event
import com.pdyjak.powerampwear.music_browser.AlbumSelectedEventArgs
import com.pdyjak.powerampwear.music_browser.ArtistSelectedEventArgs
import com.pdyjak.powerampwear.music_browser.CategorySelectedEventArgs
import com.pdyjak.powerampwear.music_browser.FileSelectedEventArgs
import com.pdyjak.powerampwear.music_browser.FolderSelectedEventArgs
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator
import com.pdyjak.powerampwear.music_browser.albums.AlbumItem
import com.pdyjak.powerampwear.music_browser.artists.ArtistItem
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem
import com.pdyjak.powerampwear.music_browser.files.FileItem
import com.pdyjak.powerampwear.music_browser.folders.FolderItem

class MusicLibraryNavigatorImpl : MusicLibraryNavigator {
    override val onCategorySelected: Event<CategorySelectedEventArgs> = Event()
    override val onFolderSelected: Event<FolderSelectedEventArgs> = Event()
    override val onAlbumSelected: Event<AlbumSelectedEventArgs> = Event()
    override val onArtistSelected: Event<ArtistSelectedEventArgs> = Event()
    override val onFileSelected: Event<FileSelectedEventArgs> = Event()

    override fun selectCategory(item: CategoryItem, fromPlayer: Boolean, scrollTo: String?) {
        onCategorySelected(CategorySelectedEventArgs(item, fromPlayer, scrollTo))
    }

    override fun selectFolder(item: FolderItem, fromPlayer: Boolean, scrollTo: String?) {
        onFolderSelected(FolderSelectedEventArgs(item, fromPlayer, scrollTo))
    }

    override fun selectAlbum(item: AlbumItem, fromPlayer: Boolean, scrollTo: String?) {
        onAlbumSelected(AlbumSelectedEventArgs(item, fromPlayer, scrollTo))
    }

    override fun selectFile(item: FileItem, fromPlayer: Boolean) {
        onFileSelected(FileSelectedEventArgs(item, fromPlayer))
    }

    override fun selectArtist(item: ArtistItem, fromPlayer: Boolean) {
        onArtistSelected(ArtistSelectedEventArgs(item, fromPlayer))
    }
}
