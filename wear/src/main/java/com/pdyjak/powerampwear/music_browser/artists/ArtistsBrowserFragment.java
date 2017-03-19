package com.pdyjak.powerampwear.music_browser.artists;

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
import com.pdyjak.powerampwearcommon.requests.RequestsPaths;
import com.pdyjak.powerampwearcommon.responses.Artist;
import com.pdyjak.powerampwearcommon.responses.ArtistsResponse;

import java.util.ArrayList;
import java.util.List;

public class ArtistsBrowserFragment extends BrowserFragmentBase implements MessageListener {

    private static class ArtistsViewHolderFactory implements ViewHolderFactory {
        @NonNull
        @Override
        public ItemViewHolder createViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.basic_two_line_item, parent, false
            );
            return new ArtistViewHolder(view);
        }
    }

    @NonNull
    @Override
    protected ViewHolderFactory createViewHolderFactory() {
        return new ArtistsViewHolderFactory();
    }

    @Override
    protected boolean tryRestoreCachedItems() {
        ArtistsResponse cached = getMusicLibraryCache().getArtists();
        if (cached != null) {
            setItems(transform(cached.getArtists()));
            return true;
        }
        return false;
    }

    @Override
    protected void fetchItems() {
        MessageExchangeHelper helper = getMessageExchangeHelper();
        helper.addMessageListenerWeakly(this);
        helper.sendRequest(RequestsPaths.GET_ARTISTS);
    }

    @Override
    public void onPause() {
        super.onPause();
        getMessageExchangeHelper().removeMessageListener(this);
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (ArtistsResponse.PATH.equals(messageEvent.getPath())) {
            getMessageExchangeHelper().removeMessageListener(this);
            byte[] bytes = messageEvent.getData();
            if (bytes == null) return;
            ArtistsResponse response = ArtistsResponse.fromBytes(bytes);
            if (response != null) {
                setItems(transform(response.getArtists()));
                getMusicLibraryCache().update(response);
            }
        }
    }

    private List<Clickable> transform(@NonNull List<Artist> artists) {
        MusicLibraryNavigator navigator = getMusicLibraryNavigator();
        List<Clickable> items = new ArrayList<>();
        for (Artist artist : artists) {
            items.add(new ArtistItem(navigator, artist.id, artist.name, artist.songsCount));
        }
        return items;
    }
}
