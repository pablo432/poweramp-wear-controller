package com.pdyjak.powerampwear

import android.content.ContentResolver
import android.net.Uri
import android.text.TextUtils
import com.maxmpz.poweramp.player.PowerampAPI
import com.maxmpz.poweramp.player.TableDefs
import com.pdyjak.powerampwearcommon.responses.Album
import com.pdyjak.powerampwearcommon.responses.AlbumsResponse
import com.pdyjak.powerampwearcommon.responses.Artist
import com.pdyjak.powerampwearcommon.responses.ArtistsResponse
import com.pdyjak.powerampwearcommon.responses.File
import com.pdyjak.powerampwearcommon.responses.FilesListResponse
import com.pdyjak.powerampwearcommon.responses.FindParentResponse
import com.pdyjak.powerampwearcommon.responses.Folder
import com.pdyjak.powerampwearcommon.responses.FoldersListResponse
import com.pdyjak.powerampwearcommon.responses.Parent
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.TimeUnit

internal class TracksProvider(private val contentResolver: ContentResolver) {

    companion object {
        private val CACHE_VALIDITY = TimeUnit.MINUTES.toMillis(30)
    }

    private object Uris {
        val folders: Uri get() = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("folders").build()

        val artistsAlbums: Uri get() = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("artists_albums").build()

        val artists: Uri get() = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("artists").build()

        val queue: Uri get() = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("queue").build()

        val intermixedFilesFolders: Uri get() = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("folders")
                .appendEncodedPath("files").build()

        fun forFolder(folderId: String): Uri {
            return PowerampAPI.ROOT_URI.buildUpon()
                    .appendEncodedPath("folders")
                    .appendEncodedPath(folderId)
                    .appendEncodedPath("files").build()
        }

        fun forAlbum(albumId: String): Uri {
            return PowerampAPI.ROOT_URI.buildUpon()
                    .appendEncodedPath("albums")
                    .appendEncodedPath(albumId)
                    .appendEncodedPath("files").build()
        }

        fun forTableName(tableName: String): Uri {
            return PowerampAPI.ROOT_URI.buildUpon()
                    .appendEncodedPath(tableName).build()
        }
    }

    private val mArtistCache = HashMap<String, String>()
    private val mAlbumCache = HashMap<String, String>()
    private val mIntermixedFilesCache = HashMap<String, File>()
    private var mCacheUpdateTime: Long = 0

    fun getQueueParent(title: String?): FindParentResponse? {
        return FindParentResponse(Parent.forQueue(), title)
    }

    fun getAllTracks(title: String?): FindParentResponse? {
        val allTracks = getAllTracksInternal() ?: return null
        return if (allTracks.isEmpty()) null else FindParentResponse(null, title)
    }

    fun getFilesInCurrentAlbum(albumName: String, title: String?): FindParentResponse? {
        if (TextUtils.isEmpty(albumName)) return null
        val albums = getAlbumsInternal(null) ?: return null
        val candidate = albums.find { album -> album.name == albumName } ?: return null
        return FindParentResponse(Parent(candidate.id, Parent.Type.Album), title)
    }

    fun getFilesInDirectory(file: java.io.File, title: String?): FindParentResponse? {
        val folderName = file.name
        if (TextUtils.isEmpty(folderName)) return null
        val folders = getFolders() ?: return null
        val candidate = folders.find { folder -> folder.name == folderName } ?: return null
        return FindParentResponse(Parent(candidate.id, Parent.Type.Folder), title)
    }

    val availableFolders: FoldersListResponse?
        get() {
            val folders = getFolders() ?: return null
            return FoldersListResponse(folders)
        }

    val queue: FilesListResponse?
        get() {
            val queue = getQueueInternal() ?: return null
            return FilesListResponse(Parent.forQueue(), queue)
        }

    val allTracks: FilesListResponse?
        get() {
            val files = getAllTracksInternal() ?: return null
            return FilesListResponse(null, files)
        }

    val artists: ArtistsResponse?
        get() {
            val c = contentResolver.query(Uris.artists, arrayOf(
                    TableDefs.Artists._ID,
                    TableDefs.Artists.ARTIST,
                    TableDefs.Artists.COUNT_FILES),
                    null, null, null)
                    ?: return null
            val artists = ArrayList<Artist>()
            c.moveToFirst()
            while (!c.isAfterLast) {
                val id = c.getString(0)
                val name = c.getString(1)
                val songsCount = c.getInt(2)
                artists.add(Artist(id, name, songsCount))
                c.moveToNext()
            }
            c.close()
            return ArtistsResponse(artists)
        }

    fun getAlbums(parent: Parent?): AlbumsResponse? {
        val albums = getAlbumsInternal(parent) ?: return null
        return AlbumsResponse(parent, albums)
    }

    fun getFilesInDirectory(folderId: String): FilesListResponse? {
        val parent = Parent(folderId, Parent.Type.Folder)
        val files = extractFiles(Uris.forFolder(folderId)) ?: return null
        return FilesListResponse(parent, files)
    }

    fun getFilesInAlbum(albumId: String): FilesListResponse? {
        val parent = Parent(albumId, Parent.Type.Album)
        val files = extractFiles(Uris.forAlbum(albumId)) ?: return null
        return FilesListResponse(parent, files)
    }

    private fun getFolders(): List<Folder>? {
        val c = contentResolver.query(Uris.folders, arrayOf(
                TableDefs.Folders._ID,
                TableDefs.Folders.NAME,
                TableDefs.Folders.PARENT_NAME),
                null, null, null)
                ?: return null
        val folders = ArrayList<Folder>()
        c.moveToFirst()
        while (!c.isAfterLast) {
            folders.add(Folder(c.getString(0), c.getString(1), c.getString(2)))
            c.moveToNext()
        }
        c.close()
        return folders
    }

    private fun getAlbumsInternal(parent: Parent?): List<Album>? {
        val selection = if (parent === null) null else TableDefs.ArtistAlbums.ARTIST_ID + "=?"
        val selectionArgs = if (parent === null) null else arrayOf(parent.id)
        val c = contentResolver.query(Uris.artistsAlbums, arrayOf(
                TableDefs.ArtistAlbums._ID,
                TableDefs.ArtistAlbums.ALBUM_ID,
                TableDefs.ArtistAlbums.ARTIST_ID),
                selection, selectionArgs, null)
                ?: return null
        val albums = ArrayList<Album>()
        c.moveToFirst()
        while (!c.isAfterLast) {
            val albumId = c.getString(1)
            val artistId = c.getString(2)
            val albumName = getAlbumNameById(albumId)
            val artist = getArtistNameById(artistId) ?: ""
            if (albumName !== null) albums.add(Album(albumId, albumName, artist))
            c.moveToNext()
        }
        c.close()
        return albums
    }

    private fun getQueueInternal(): List<File>? {
        val c = contentResolver.query(Uris.queue, arrayOf(
                TableDefs.Queue._ID,
                TableDefs.Queue.FOLDER_FILE_ID),
                null, null, null)
                ?: return null
        val files = ArrayList<File>()
        val all = getFilesFoldersIntermixed()
        c.moveToFirst()
        while (!c.isAfterLast) {
            val contextualId = c.getString(0)
            val folderFileId = c.getString(1)
            if (TextUtils.isEmpty(folderFileId)) {
                c.moveToNext()
                continue
            }
            val found = all[folderFileId]
            if (found !== null) {
                found.contextualId = contextualId
                files.add(found)
            }
            c.moveToNext()
        }
        c.close()
        return files
    }

    private fun getFilesFoldersIntermixed(): Map<String, File> {
        if (mCacheUpdateTime + CACHE_VALIDITY > System.currentTimeMillis()) {
            return mIntermixedFilesCache
        }
        val files = extractFiles(Uris.intermixedFilesFolders) ?: return mIntermixedFilesCache
        mIntermixedFilesCache.clear()
        for (file in files) {
            mIntermixedFilesCache.put(file.id, file)
        }
        mCacheUpdateTime = System.currentTimeMillis()
        return mIntermixedFilesCache
    }

    private fun getAllTracksInternal(): List<File>? {
        val uri = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("files")
                .build()
        return extractFiles(uri)
    }


    private fun extractFiles(uri: Uri): List<File>? {
        val c = contentResolver.query(uri, arrayOf(
                TableDefs.Files._ID,
                TableDefs.Files.TITLE_TAG,
                TableDefs.Files.ARTIST_ID,
                TableDefs.Files.ALBUM_ID,
                TableDefs.Files.DURATION),
                null, null, null)
                ?: return null
        val files = ArrayList<File>()
        c.moveToFirst()
        while (!c.isAfterLast) {
            val fileId = c.getString(0)
            val title = c.getString(1)
            val artistId = c.getString(2)
            val artist = getArtistNameById(artistId) ?: ""
            val albumId = c.getString(3)
            val album = getAlbumNameById(albumId) ?: ""
            val duration = c.getLong(4) / 1000
            files.add(File(fileId, title, artist, album, duration))
            c.moveToNext()
        }
        c.close()
        return files
    }

    private fun getArtistNameById(id: String): String? {
        val cached = mArtistCache[id]
        if (cached !== null) return cached
        val value = getStringById(id,
                TableDefs.Artists.TABLE,
                TableDefs.Artists._ID,
                TableDefs.Artists.ARTIST)
        if (value !== null) mArtistCache.put(id, value)
        return value
    }

    private fun getAlbumNameById(id: String): String? {
        val cached = mAlbumCache[id]
        if (cached !== null) return cached
        val value = getStringById(id,
                TableDefs.Albums.TABLE,
                TableDefs.Albums._ID,
                TableDefs.Albums.ALBUM)
        if (value !== null) mAlbumCache.put(id, value)
        return value
    }

    private fun getStringById(
            id: String,
            table: String,
            idColumnName: String,
            wantedColumn: String): String? {
        // oh boy
        try {
            val c = contentResolver.query(Uris.forTableName(table),
                    arrayOf(idColumnName, wantedColumn),
                    idColumnName + "=?", arrayOf(id), null)
                    ?: return null
            var name: String? = null
            if (c.moveToFirst()) name = c.getString(1)
            c.close()
            return name
        } catch (t: Throwable) {
            return null
        }
    }
}