package com.pdyjak.powerampwear.music_browser.folders;

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
import com.pdyjak.powerampwearcommon.responses.Folder;
import com.pdyjak.powerampwearcommon.responses.FoldersListResponse;

import java.util.ArrayList;
import java.util.List;

public class FoldersBrowserFragment extends BrowserFragmentBase implements MessageListener {

    private static class FoldersViewHolderFactory implements ViewHolderFactory {
        @NonNull
        @Override
        public ItemViewHolder createViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.basic_two_line_item, parent, false
            );
            return new FolderViewHolder(view);
        }
    }

    @NonNull
    @Override
    protected ViewHolderFactory createViewHolderFactory() {
        return new FoldersViewHolderFactory();
    }

    @Override
    protected boolean tryRestoreCachedItems() {
        FoldersListResponse cached = getMusicLibraryCache().getFoldersList();
        if (cached != null) {
            setItems(transform(cached.getFoldersList()));
            return true;
        }
        return false;
    }

    @Override
    protected void fetchItems() {
        MessageExchangeHelper helper = getMessageExchangeHelper();
        helper.addMessageListenerWeakly(this);
        helper.sendRequest(RequestsPaths.GET_FOLDERS);
    }

    @Override
    public void onPause() {
        super.onPause();
        getMessageExchangeHelper().removeMessageListener(this);
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (FoldersListResponse.PATH.equals(messageEvent.getPath())) {
            getMessageExchangeHelper().removeMessageListener(this);
            byte[] bytes = messageEvent.getData();
            if (bytes == null) return;
            FoldersListResponse response = FoldersListResponse.fromBytes(bytes);
            if (response != null) {
                getMusicLibraryCache().update(response);
                setItems(transform(response.getFoldersList()));
            }
        }
    }

    private List<Clickable> transform(@NonNull List<Folder> folders) {
        MusicLibraryNavigator navigator = getMusicLibraryNavigator();
        List<Clickable> items = new ArrayList<>();
        for (Folder folder : folders) {
            items.add(new FolderItem(navigator, folder.id, folder.name, folder.parentName));
        }
        return items;
    }
}
