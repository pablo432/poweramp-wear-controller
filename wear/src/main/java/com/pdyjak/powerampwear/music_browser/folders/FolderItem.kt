package com.pdyjak.powerampwear.music_browser.folders

import com.pdyjak.powerampwear.music_browser.Clickable
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator

class FolderItem(private val mMusicLibraryNavigator: MusicLibraryNavigator, val id: String,
                 internal val name: String?, internal val parentName: String?)
    : Clickable {

    override fun onClicked() {
        mMusicLibraryNavigator.selectFolder(this, false, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null || javaClass != other.javaClass) return false

        val that = other as FolderItem

        if (id != that.id) return false
        if (name != that.name) return false
        return parentName == that.parentName
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (parentName?.hashCode() ?: 0)
        return result
    }
}
