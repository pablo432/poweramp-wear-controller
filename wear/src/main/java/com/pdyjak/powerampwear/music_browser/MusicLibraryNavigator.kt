package com.pdyjak.powerampwear.music_browser

import com.pdyjak.powerampwear.common.Event
import com.pdyjak.powerampwear.music_browser.albums.AlbumItem
import com.pdyjak.powerampwear.music_browser.artists.ArtistItem
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem
import com.pdyjak.powerampwear.music_browser.files.FileItem
import com.pdyjak.powerampwear.music_browser.folders.FolderItem

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
interface MusicLibraryNavigator {

    val onCategorySelected: Event<ItemSelectedEventArgs<CategoryItem>>
    val onFolderSelected: Event<ItemSelectedEventArgs<FolderItem>>
    val onAlbumSelected: Event<ItemSelectedEventArgs<AlbumItem>>
    val onArtistSelected: Event<ItemSelectedEventArgs<ArtistItem>>
    val onFileSelected: Event<ItemSelectedEventArgs<FileItem>>

    fun selectCategory(item: CategoryItem, fromPlayer: Boolean, scrollTo: String?)
    fun selectFolder(item: FolderItem, fromPlayer: Boolean, scrollTo: String?)
    fun selectAlbum(item: AlbumItem, fromPlayer: Boolean, scrollTo: String?)
    fun selectFile(item: FileItem, fromPlayer: Boolean)
    fun selectArtist(item: ArtistItem, fromPlayer: Boolean)
}
