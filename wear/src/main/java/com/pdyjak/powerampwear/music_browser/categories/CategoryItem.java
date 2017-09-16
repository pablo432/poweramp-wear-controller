package com.pdyjak.powerampwear.music_browser.categories;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.pdyjak.powerampwear.music_browser.Clickable;
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;

public class CategoryItem implements Clickable {

    @NonNull
    private final MusicLibraryNavigator mMusicLibraryNavigator;
    @NonNull
    public final String path;
    final int stringId;
    final int iconId;

    CategoryItem(@NonNull MusicLibraryNavigator helper, @NonNull String path, @StringRes int stringId,
            @DrawableRes int iconId) {
        mMusicLibraryNavigator = helper;
        this.path = path;
        this.stringId = stringId;
        this.iconId = iconId;
    }

    @Override
    public void onClicked() {
        mMusicLibraryNavigator.selectCategory(this, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryItem that = (CategoryItem) o;
        if (stringId != that.stringId) return false;
        if (iconId != that.iconId) return false;
        if (!mMusicLibraryNavigator.equals(that.mMusicLibraryNavigator)) return false;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = mMusicLibraryNavigator.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + stringId;
        result = 31 * result + iconId;
        return result;
    }
}
