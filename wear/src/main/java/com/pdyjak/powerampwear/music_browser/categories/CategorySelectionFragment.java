package com.pdyjak.powerampwear.music_browser.categories;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.music_browser.BrowserFragmentBase;
import com.pdyjak.powerampwear.music_browser.Clickable;
import com.pdyjak.powerampwear.music_browser.ItemViewHolder;
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;
import com.pdyjak.powerampwear.music_browser.ViewHolderFactory;
import com.pdyjak.powerampwearcommon.requests.GetAlbumsRequest;
import com.pdyjak.powerampwearcommon.requests.GetFilesRequest;
import com.pdyjak.powerampwearcommon.requests.RequestsPaths;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectionFragment extends BrowserFragmentBase {

    private static class CategoryViewHolderFactory implements ViewHolderFactory {
        @NonNull
        @Override
        public ItemViewHolder createViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,
                    parent, false);
            return new CategoryViewHolder(view);
        }
    }

    @NonNull
    @Override
    protected ViewHolderFactory createViewHolderFactory() {
        return new CategoryViewHolderFactory();
    }

    @Override
    protected boolean tryRestoreCachedItems() {
        setItems(createCategories());
        return true;
    }

    @Override
    protected void fetchItems() {
        tryRestoreCachedItems();
    }

    @NonNull
    private List<Clickable> createCategories() {
        MusicLibraryNavigator navigator = getMusicLibraryNavigator();
        List<Clickable> items = new ArrayList<>();
        items.add(new CategoryItem(navigator, RequestsPaths.GET_FOLDERS, R.string.folders,
                R.drawable.ic_folder_white));

        items.add(new CategoryItem(navigator, GetFilesRequest.PATH, R.string.all_tracks,
                R.drawable.ic_library_music_white));

        items.add(new CategoryItem(navigator, GetAlbumsRequest.PATH, R.string.albums,
                R.drawable.ic_album_white));

        items.add(new CategoryItem(navigator, RequestsPaths.GET_ARTISTS, R.string.artists,
                R.drawable.ic_artists_white));

        items.add(new CategoryItem(navigator, "queue", R.string.queue,
                R.drawable.ic_queue_music_white_48dp));

        // TODO: :)
        // items.add(new CategoryItem(navigator, RequestsPaths.GET_PLAYLISTS, R.string.playlists,
        //         R.drawable.ic_playlist_white));
        return items;
    }
}
