package com.pdyjak.powerampwear.music_browser

import com.pdyjak.powerampwear.music_browser.albums.AlbumItem
import com.pdyjak.powerampwear.music_browser.artists.ArtistItem
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem
import com.pdyjak.powerampwear.music_browser.files.FileItem
import com.pdyjak.powerampwear.music_browser.folders.FolderItem

data class ItemSelectedEventArgs<out T : Clickable>(
        val item: T, val fromPlayer: Boolean, val scrollTo: String? = null)

typealias CategorySelectedEventArgs = ItemSelectedEventArgs<CategoryItem>
typealias FolderSelectedEventArgs = ItemSelectedEventArgs<FolderItem>
typealias AlbumSelectedEventArgs = ItemSelectedEventArgs<AlbumItem>
typealias ArtistSelectedEventArgs = ItemSelectedEventArgs<ArtistItem>
typealias FileSelectedEventArgs = ItemSelectedEventArgs<FileItem>