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
        onCategorySelected.notifyEventChanged(CategorySelectedEventArgs(item, fromPlayer, scrollTo))
    }

    override fun selectFolder(item: FolderItem, fromPlayer: Boolean, scrollTo: String?) {
        onFolderSelected.notifyEventChanged(FolderSelectedEventArgs(item, fromPlayer, scrollTo))
    }

    override fun selectAlbum(item: AlbumItem, fromPlayer: Boolean, scrollTo: String?) {
        onAlbumSelected.notifyEventChanged(AlbumSelectedEventArgs(item, fromPlayer, scrollTo))
    }

    override fun selectFile(item: FileItem, fromPlayer: Boolean) {
        onFileSelected.notifyEventChanged(FileSelectedEventArgs(item, fromPlayer))
    }

    override fun selectArtist(item: ArtistItem, fromPlayer: Boolean) {
        onArtistSelected.notifyEventChanged(ArtistSelectedEventArgs(item, fromPlayer))
    }
}
