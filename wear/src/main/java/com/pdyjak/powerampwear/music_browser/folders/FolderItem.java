package com.pdyjak.powerampwear.music_browser.folders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwear.music_browser.Clickable;
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;

public class FolderItem implements Clickable {
    @NonNull
    private final MusicLibraryNavigator mMusicLibraryNavigator;

    @NonNull
    public final String id;
    @Nullable
    final String name;
    @Nullable
    final String parentName;

    public FolderItem(@NonNull MusicLibraryNavigator helper, @NonNull String id, @Nullable String name,
                      @Nullable String parentName) {
        mMusicLibraryNavigator = helper;
        this.id = id;
        this.name = name;
        this.parentName = parentName;
    }

    @Override
    public void onClicked() {
        mMusicLibraryNavigator.selectFolder(this, false, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FolderItem that = (FolderItem) o;

        if (!id.equals(that.id)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return parentName != null ? parentName.equals(that.parentName) : that.parentName == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (parentName != null ? parentName.hashCode() : 0);
        return result;
    }
}
