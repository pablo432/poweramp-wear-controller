package com.pdyjak.powerampwear.music_browser.albums;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.wearable.MessageEvent;
import com.pdyjak.powerampwear.MessageExchangeHelper;
import com.pdyjak.powerampwear.MessageListener;
import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.music_browser.BrowserFragmentBase;
import com.pdyjak.powerampwear.music_browser.Clickable;
import com.pdyjak.powerampwear.music_browser.ItemViewHolder;
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator;
import com.pdyjak.powerampwear.music_browser.ViewHolderFactory;
import com.pdyjak.powerampwearcommon.requests.GetAlbumsRequest;
import com.pdyjak.powerampwearcommon.responses.Album;
import com.pdyjak.powerampwearcommon.responses.AlbumsResponse;
import com.pdyjak.powerampwearcommon.responses.Parent;

import java.util.ArrayList;
import java.util.List;

public class AlbumsBrowserFragment extends BrowserFragmentBase implements MessageListener {
    public static final String PARENT_KEY = "parent";

    private static class AlbumsViewHolderFactory implements ViewHolderFactory {
        @NonNull
        @Override
        public ItemViewHolder createViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.basic_two_line_item, parent, false
            );
            return new AlbumViewHolder(view);
        }
    }

    private Parent mParent;

    @NonNull
    @Override
    protected ViewHolderFactory createViewHolderFactory() {
        return new AlbumsViewHolderFactory();
    }

    @Override
    protected boolean tryRestoreCachedItems() {
        AlbumsResponse cached = getMusicLibraryCache().getAlbums(mParent);
        if (cached != null) {
            setItems(transform(cached.getAlbums()));
            return true;
        }
        return false;
    }

    @Override
    protected void fetchItems() {
        MessageExchangeHelper helper = getMessageExchangeHelper();
        helper.addMessageListenerWeakly(this);
        GetAlbumsRequest request = new GetAlbumsRequest(mParent);
        helper.sendRequest(GetAlbumsRequest.PATH, request);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("Should never happen");
        }
        mParent = args.getParcelable(PARENT_KEY);
    }

    @Override
    public void onPause() {
        super.onPause();
        getMessageExchangeHelper().removeMessageListener(this);
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (AlbumsResponse.PATH.equals(messageEvent.getPath())) {
            getMessageExchangeHelper().removeMessageListener(this);
            byte[] bytes = messageEvent.getData();
            if (bytes == null) return;
            AlbumsResponse response = AlbumsResponse.fromBytes(bytes);
            if (response != null) {
                setItems(transform(response.getAlbums()));
                getMusicLibraryCache().update(response);
            }
        }
    }

    private List<Clickable> transform(@NonNull List<Album> albums) {
        MusicLibraryNavigator navigator = getMusicLibraryNavigator();
        List<Clickable> items = new ArrayList<>();
        for (Album album : albums) {
            items.add(new AlbumItem(navigator, album.id, album.name, album.artistName));
        }
        return items;
    }
}
