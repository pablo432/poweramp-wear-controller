package com.pdyjak.powerampwear.music_browser.files

import com.pdyjak.powerampwear.music_browser.Clickable
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator
import com.pdyjak.powerampwearcommon.responses.Parent

class FileItem(private val mMusicLibraryNavigator: MusicLibraryNavigator, val parent: Parent?,
               val trackId: String, internal val title: String?, internal val artist: String?,
               internal val album: String?, internal val duration: Long, val contextualId: String?)
    : Clickable {

    override fun onClicked() {
        mMusicLibraryNavigator.selectFile(this, false)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null || javaClass != other.javaClass) return false

        val fileItem = other as FileItem

        if (parent != fileItem.parent) return false
        if (trackId != fileItem.trackId) return false
        if (title != fileItem.title) return false
        if (artist != fileItem.artist) return false
        if (album != fileItem.album) return false
        return duration == fileItem.duration
    }

    override fun hashCode(): Int {
        var result = parent?.hashCode() ?: 0
        result = 31 * result + trackId.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (artist?.hashCode() ?: 0)
        result = 31 * result + (album?.hashCode() ?: 0)
        result = 31 * result + (duration xor duration.ushr(32)).toInt()
        return result
    }
}
