package com.pdyjak.powerampwear;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pdyjak.powerampwear.music_browser.MusicLibraryCache;
import com.pdyjak.powerampwearcommon.responses.AlbumsResponse;
import com.pdyjak.powerampwearcommon.responses.ArtistsResponse;
import com.pdyjak.powerampwearcommon.responses.FilesListResponse;
import com.pdyjak.powerampwearcommon.responses.FoldersListResponse;
import com.pdyjak.powerampwearcommon.responses.Parent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO: keep this in some persistent storage
 */
class MusicLibraryCacheImpl implements MusicLibraryCache {
    @NonNull
    private final Set<InvalidationListener> mInvalidationListeners = new HashSet<>();
    @NonNull
    private final Map<Parent, FilesListResponse> mFilesResponseMap = new HashMap<>();
    @Nullable
    private FoldersListResponse mFolders;
    @NonNull
    private final Map<Parent, AlbumsResponse> mAlbumsResponsesMap = new HashMap<>();
    @Nullable
    private ArtistsResponse mArtists;

    @Override
    public void update(@NonNull FoldersListResponse response) {
        mFolders = response;
    }

    @Override
    public void update(@NonNull FilesListResponse response) {
        mFilesResponseMap.put(response.parent, response);
    }

    @Override
    public void update(@NonNull AlbumsResponse response) {
        mAlbumsResponsesMap.put(response.parent, response);
    }

    @Override
    public void update(@NonNull ArtistsResponse response) {
        mArtists = response;
    }

    @Override
    public void invalidate() {
        mFilesResponseMap.clear();
        mFolders = null;
        mAlbumsResponsesMap.clear();
        mArtists = null;
        Set<InvalidationListener> copy = new HashSet<>(mInvalidationListeners);
        for (InvalidationListener listener : copy) {
            listener.onCacheInvalidated();
        }
    }

    @Nullable
    @Override
    public FoldersListResponse getFoldersList() {
        return mFolders;
    }

    @Nullable
    @Override
    public FilesListResponse getFilesList(@Nullable Parent parent) {
        return mFilesResponseMap.get(parent);
    }

    @Nullable
    @Override
    public AlbumsResponse getAlbums(@Nullable Parent parent) {
        return mAlbumsResponsesMap.get(parent);
    }

    @Nullable
    @Override
    public ArtistsResponse getArtists() {
        return mArtists;
    }

    @Override
    public void addInvalidationListener(@NonNull InvalidationListener listener) {
        mInvalidationListeners.add(listener);
    }

    @Override
    public void removeInvalidationListener(@NonNull InvalidationListener listener) {
        mInvalidationListeners.remove(listener);
    }
}
