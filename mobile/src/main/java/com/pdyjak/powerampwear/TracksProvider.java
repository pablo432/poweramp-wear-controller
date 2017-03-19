package com.pdyjak.powerampwear;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.maxmpz.poweramp.player.PowerampAPI;
import com.maxmpz.poweramp.player.TableDefs;
import com.pdyjak.powerampwearcommon.responses.Album;
import com.pdyjak.powerampwearcommon.responses.AlbumsResponse;
import com.pdyjak.powerampwearcommon.responses.Artist;
import com.pdyjak.powerampwearcommon.responses.ArtistsResponse;
import com.pdyjak.powerampwearcommon.responses.File;
import com.pdyjak.powerampwearcommon.responses.FilesListResponse;
import com.pdyjak.powerampwearcommon.responses.Folder;
import com.pdyjak.powerampwearcommon.responses.FoldersListResponse;
import com.pdyjak.powerampwearcommon.responses.Parent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TracksProvider {
    @NonNull
    private final ContentResolver mContentResolver;
    @NonNull
    private final Map<String, String> mArtistCache = new HashMap<>();
    @NonNull
    private final Map<String, String> mAlbumCache = new HashMap<>();

    TracksProvider(@NonNull ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    FoldersListResponse getAvailableFolders() {
        Uri uri = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("folders")
                .build();
        Cursor c = mContentResolver.query(uri, new String[] {
                TableDefs.Folders._ID, TableDefs.Folders.NAME, TableDefs.Folders.PARENT_NAME
        }, null, null, null);
        if (c == null) return null;
        List<Folder> folders = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            folders.add(new Folder(c.getString(0), c.getString(1), c.getString(2)));
        }
        c.close();
        return new FoldersListResponse(folders);
    }

    AlbumsResponse getAlbums(@Nullable Parent parent) {
        Uri uri = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("artists_albums")
                .build();
        String selection = parent == null ? null : TableDefs.ArtistAlbums.ARTIST_ID + "=?";
        String[] selectionArgs = parent == null ? null : new String[] { parent.id };
        Cursor c = mContentResolver.query(uri, new String[] {
                TableDefs.ArtistAlbums._ID,
                TableDefs.ArtistAlbums.ALBUM_ID,
                TableDefs.ArtistAlbums.ARTIST_ID
        }, selection, selectionArgs, null);
        if (c == null) return null;
        List<Album> albums = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String albumId = c.getString(1);
            String artistId = c.getString(2);
            String albumName = getAlbumNameById(albumId);
            String artist = getArtistNameById(artistId);
            albums.add(new Album(albumId, albumName, artist));
        }
        c.close();
        return new AlbumsResponse(parent, albums);
    }

    ArtistsResponse getArtists() {
        Uri uri = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("artists")
                .build();
        Cursor c = mContentResolver.query(uri, new String[] {
                TableDefs.Artists._ID,
                TableDefs.Artists.ARTIST, TableDefs.Artists.COUNT_FILES
        }, null, null, null);
        if (c == null) return null;
        List<Artist> artists = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String id = c.getString(0);
            String name = c.getString(1);
            int songsCount = c.getInt(2);
            artists.add(new Artist(id, name, songsCount));
        }
        c.close();
        return new ArtistsResponse(artists);
    }

    FilesListResponse getAllTracks() {
        Uri uri = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("files")
                .build();
        List<File> files = extractFiles(uri);
        if (files == null) return null;
        return new FilesListResponse(null, files);
    }

    FilesListResponse getFilesInDirectory(@NonNull String folderId) {
        Uri uri = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("folders")
                .appendEncodedPath(folderId)
                .appendEncodedPath("files")
                .build();
        Parent parent = new Parent(folderId, Parent.Type.Folder);
        List<File> files = extractFiles(uri);
        if (files == null) return null;
        return new FilesListResponse(parent, files);
    }

    FilesListResponse getFilesInAlbum(@NonNull String albumId) {
        Uri uri = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath("albums")
                .appendEncodedPath(albumId)
                .appendEncodedPath("files")
                .build();
        Parent parent = new Parent(albumId, Parent.Type.Album);
        List<File> files = extractFiles(uri);
        if (files == null) return null;
        return new FilesListResponse(parent, files);
    }

    private List<File> extractFiles(@NonNull Uri uri) {
        Cursor c = mContentResolver.query(uri, new String[] {
                TableDefs.Files._ID, TableDefs.Files.TITLE_TAG, TableDefs.Files.ARTIST_ID,
                TableDefs.Files.ALBUM_ID, TableDefs.Files.DURATION
        }, null, null, null);
        if (c == null) return null;
        List<File> files = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String fileId = c.getString(0);
            String title = c.getString(1);
            String artistId = c.getString(2);
            String artist = getArtistNameById(artistId);
            String albumId = c.getString(3);
            String album = getAlbumNameById(albumId);
            long duration = c.getLong(4) / 1000;
            files.add(new File(fileId, title, artist, album, duration));
        }
        c.close();
        return files;
    }

    private String getArtistNameById(@NonNull String id) {
        String cached = mArtistCache.get(id);
        if (cached != null) return cached;
        String value = getStringById(id,
                TableDefs.Artists.TABLE,
                TableDefs.Artists._ID,
                TableDefs.Artists.ARTIST);
        mArtistCache.put(id, value);
        return value;
    }

    private String getAlbumNameById(@NonNull String id) {
        String cached = mAlbumCache.get(id);
        if (cached != null) return cached;
        String value = getStringById(id,
                TableDefs.Albums.TABLE,
                TableDefs.Albums._ID,
                TableDefs.Albums.ALBUM);
        mAlbumCache.put(id, value);
        return value;
    }

    private String getStringById(
            @NonNull String id,
            @NonNull String table,
            @NonNull String idColumnName,
            @NonNull String wantedColumn) {
        Uri uri = PowerampAPI.ROOT_URI.buildUpon()
                .appendEncodedPath(table)
                .build();
        Cursor c = mContentResolver.query(uri, new String[] {
                idColumnName, wantedColumn
        }, idColumnName + "=?", new String[] {id}, null);
        if (c == null) return null;
        String name = null;
        if (c.moveToFirst()) name = c.getString(1);
        c.close();
        return name;
    }
}
