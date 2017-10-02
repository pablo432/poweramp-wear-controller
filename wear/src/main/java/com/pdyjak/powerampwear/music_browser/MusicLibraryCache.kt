package com.pdyjak.powerampwear.music_browser

import com.pdyjak.powerampwear.common.SimpleEvent
import com.pdyjak.powerampwearcommon.responses.AlbumsResponse
import com.pdyjak.powerampwearcommon.responses.ArtistsResponse
import com.pdyjak.powerampwearcommon.responses.FilesListResponse
import com.pdyjak.powerampwearcommon.responses.FoldersListResponse
import com.pdyjak.powerampwearcommon.responses.Parent

interface MusicLibraryCache {
    fun update(response: FoldersListResponse)
    fun update(response: FilesListResponse)
    fun update(response: AlbumsResponse)
    fun update(response: ArtistsResponse)
    fun invalidate()

    val foldersList: FoldersListResponse?
    fun getFilesList(parent: Parent?): FilesListResponse?
    fun getAlbums(parent: Parent?): AlbumsResponse?
    val artists: ArtistsResponse?
    val onInvalidation: SimpleEvent
}
