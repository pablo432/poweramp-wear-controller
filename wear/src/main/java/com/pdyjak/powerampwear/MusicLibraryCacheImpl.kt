package com.pdyjak.powerampwear

import com.pdyjak.powerampwear.common.SimpleEvent
import com.pdyjak.powerampwear.music_browser.MusicLibraryCache
import com.pdyjak.powerampwearcommon.responses.AlbumsResponse
import com.pdyjak.powerampwearcommon.responses.ArtistsResponse
import com.pdyjak.powerampwearcommon.responses.FilesListResponse
import com.pdyjak.powerampwearcommon.responses.FoldersListResponse
import com.pdyjak.powerampwearcommon.responses.Parent

import java.util.HashMap

/**
 * TODO: keep this in some persistent storage
 */
internal class MusicLibraryCacheImpl : MusicLibraryCache {
    private val mFilesResponseMap = HashMap<Parent?, FilesListResponse>()
    private val mAlbumsResponsesMap = HashMap<Parent, AlbumsResponse>()

    override var foldersList: FoldersListResponse? = null
        private set

    override var artists: ArtistsResponse? = null
        private set

    override fun update(response: FoldersListResponse) {
        foldersList = response
    }

    override fun update(response: FilesListResponse) {
        response.parent?.let { if (it.type == Parent.Type.Queue || it.id == "queue") return }
        mFilesResponseMap.put(response.parent, response)
    }

    override fun update(response: AlbumsResponse) {
        mAlbumsResponsesMap.put(response.parent ?: return, response)
    }

    override fun update(response: ArtistsResponse) {
        artists = response
    }

    override fun invalidate() {
        mFilesResponseMap.clear()
        foldersList = null
        mAlbumsResponsesMap.clear()
        artists = null
        onInvalidation.notifyEventChanged()
    }

    override fun getFilesList(parent: Parent?): FilesListResponse? {
        return mFilesResponseMap[parent]
    }

    override fun getAlbums(parent: Parent?): AlbumsResponse? {
        return mAlbumsResponsesMap[parent]
    }

    override val onInvalidation = SimpleEvent()
}
