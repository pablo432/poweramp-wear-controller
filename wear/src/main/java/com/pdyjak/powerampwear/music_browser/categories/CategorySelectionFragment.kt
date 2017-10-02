package com.pdyjak.powerampwear.music_browser.categories

import android.view.LayoutInflater
import android.view.ViewGroup

import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.common.musicLibraryNavigator
import com.pdyjak.powerampwear.music_browser.BrowserFragmentBase
import com.pdyjak.powerampwear.music_browser.Clickable
import com.pdyjak.powerampwear.music_browser.ItemViewHolder
import com.pdyjak.powerampwear.music_browser.ViewHolderFactory
import com.pdyjak.powerampwearcommon.requests.GetAlbumsRequest
import com.pdyjak.powerampwearcommon.requests.GetFilesRequest
import com.pdyjak.powerampwearcommon.requests.RequestsPaths

import java.util.ArrayList

class CategorySelectionFragment : BrowserFragmentBase() {

    private class CategoryViewHolderFactory : ViewHolderFactory {
        override fun createViewHolder(parent: ViewGroup): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item,
                    parent, false)
            return CategoryViewHolder(view)
        }
    }

    override fun createViewHolderFactory(): ViewHolderFactory {
        return CategoryViewHolderFactory()
    }

    override fun tryRestoreCachedItems(): Boolean {
        setItems(createCategories())
        return true
    }

    override fun fetchItems() {
        tryRestoreCachedItems()
    }

    private fun createCategories(): List<Clickable> {
        val navigator = activity.musicLibraryNavigator
        val items = ArrayList<Clickable>()
        items.add(CategoryItem(navigator, RequestsPaths.GET_FOLDERS, R.string.folders,
                R.drawable.ic_folder_white))

        items.add(CategoryItem(navigator, GetFilesRequest.PATH, R.string.all_tracks,
                R.drawable.ic_library_music_white))

        items.add(CategoryItem(navigator, GetAlbumsRequest.PATH, R.string.albums,
                R.drawable.ic_album_white))

        items.add(CategoryItem(navigator, RequestsPaths.GET_ARTISTS, R.string.artists,
                R.drawable.ic_artists_white))

        items.add(CategoryItem(navigator, "queue", R.string.queue,
                R.drawable.ic_queue_music_white_48dp))

        // TODO: :)
        // items.add(new CategoryItem(navigator, RequestsPaths.GET_PLAYLISTS, R.string.playlists,
        //         R.drawable.ic_playlist_white));
        return items
    }
}
