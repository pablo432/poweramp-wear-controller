package com.pdyjak.powerampwear.music_browser.artists

import com.pdyjak.powerampwear.music_browser.Clickable
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator

class ArtistItem(private val mMusicLibraryNavigator: MusicLibraryNavigator,
                 val id: String, internal val name: String?, internal val songsCount: Int)
    : Clickable {

    override fun onClicked() {
        mMusicLibraryNavigator.selectArtist(this, false)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null || javaClass != other.javaClass) return false

        val that = other as ArtistItem
        if (songsCount != that.songsCount) return false
        if (id != that.id) return false
        return name == that.name
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + songsCount
        return result
    }
}
