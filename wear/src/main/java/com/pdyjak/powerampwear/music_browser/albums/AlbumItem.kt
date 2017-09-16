package com.pdyjak.powerampwear.music_browser.albums

import com.pdyjak.powerampwear.music_browser.Clickable
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator

class AlbumItem(private val mMusicLibraryNavigator: MusicLibraryNavigator,
                val id: String, internal val name: String?, internal val artist: String?)
    : Clickable {

    override fun onClicked() {
        mMusicLibraryNavigator.selectAlbum(this, false, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null || javaClass != other.javaClass) return false

        val albumItem = other as AlbumItem
        if (id != albumItem.id) return false
        if (name != albumItem.name) return false
        return artist == albumItem.artist
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (artist?.hashCode() ?: 0)
        return result
    }
}
