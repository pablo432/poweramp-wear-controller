package com.pdyjak.powerampwear.music_browser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwearcommon.responses.AlbumsResponse;
import com.pdyjak.powerampwearcommon.responses.ArtistsResponse;
import com.pdyjak.powerampwearcommon.responses.FilesListResponse;
import com.pdyjak.powerampwearcommon.responses.FoldersListResponse;
import com.pdyjak.powerampwearcommon.responses.Parent;

public interface MusicLibraryCache {
    interface InvalidationListener {
        void onCacheInvalidated();
    }

    void update(@NonNull FoldersListResponse response);
    void update(@NonNull FilesListResponse response);
    void update(@NonNull AlbumsResponse response);
    void update(@NonNull ArtistsResponse response);
    void invalidate();

    @Nullable
    FoldersListResponse getFoldersList();
    @Nullable
    FilesListResponse getFilesList(@Nullable Parent parent);
    @Nullable
    AlbumsResponse getAlbums(@Nullable Parent parent);
    @Nullable
    ArtistsResponse getArtists();

    void addInvalidationListener(@NonNull InvalidationListener listener);
    void removeInvalidationListener(@NonNull InvalidationListener listener);
}
