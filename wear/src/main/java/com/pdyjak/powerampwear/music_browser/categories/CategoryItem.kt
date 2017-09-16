package com.pdyjak.powerampwear.music_browser.categories

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

import com.pdyjak.powerampwear.music_browser.Clickable
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator

class CategoryItem(private val mMusicLibraryNavigator: MusicLibraryNavigator,
                   val path: String, @param:StringRes internal val stringId: Int,
                   @param:DrawableRes internal val iconId: Int)
    : Clickable {

    override fun onClicked() {
        mMusicLibraryNavigator.selectCategory(this, false, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null || javaClass != other.javaClass) return false

        val that = other as CategoryItem
        if (stringId != that.stringId) return false
        if (iconId != that.iconId) return false
        if (mMusicLibraryNavigator != that.mMusicLibraryNavigator) return false
        return path == that.path
    }

    override fun hashCode(): Int {
        var result = mMusicLibraryNavigator.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + stringId
        result = 31 * result + iconId
        return result
    }
}
