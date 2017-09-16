package com.pdyjak.powerampwear.music_browser.files;

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
import com.pdyjak.powerampwearcommon.requests.GetFilesRequest;
import com.pdyjak.powerampwearcommon.responses.File;
import com.pdyjak.powerampwearcommon.responses.FilesListResponse;
import com.pdyjak.powerampwearcommon.responses.Parent;

import java.util.ArrayList;
import java.util.List;

public class FilesBrowserFragment extends BrowserFragmentBase implements MessageListener {
    public static final String PARENT_KEY = "parent";

    private static class FilesViewHolderFactory implements ViewHolderFactory {
        @NonNull
        @Override
        public ItemViewHolder createViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.basic_two_line_item, parent, false
            );
            return new FileViewHolder(view);
        }
    }

    private Parent mParent;

    @NonNull
    @Override
    protected ViewHolderFactory createViewHolderFactory() {
        return new FilesViewHolderFactory();
    }

    @Override
    protected boolean tryRestoreCachedItems() {
        FilesListResponse cached = getMusicLibraryCache().getFilesList(mParent);
        if (cached == null) return false;
        setItems(transform(cached));
        return true;
    }

    @Override
    protected boolean shouldScrollTo(@NonNull Clickable item, @NonNull String scrollDest) {
        if (!(item instanceof FileItem)) return false;
        FileItem fileItem = (FileItem) item;
        return fileItem.title != null && fileItem.title.startsWith(scrollDest);
    }

    @Override
    protected void fetchItems() {
        GetFilesRequest request = new GetFilesRequest(mParent);
        MessageExchangeHelper helper = getMessageExchangeHelper();
        helper.addMessageListenerWeakly(this);
        helper.sendRequest(GetFilesRequest.PATH, request);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ensureParent();
    }

    @Override
    protected void onGoingToRefresh() {
        ensureParent();
    }

    private void ensureParent() {
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
        if (FilesListResponse.PATH.equals(messageEvent.getPath())) {
            getMessageExchangeHelper().removeMessageListener(this);
            byte[] bytes = messageEvent.getData();
            if (bytes == null) return;
            FilesListResponse response = FilesListResponse.fromBytes(bytes);
            if (response != null) {
                setItems(transform(response));
                getMusicLibraryCache().update(response);
            }
        }
    }

    private List<Clickable> transform(@NonNull FilesListResponse response) {
        MusicLibraryNavigator navigator = getMusicLibraryNavigator();
        List<File> files = response.getFilesList();
        List<Clickable> items = new ArrayList<>();
        for (File file : files) {
            items.add(new FileItem(navigator, response.parent, file.id, file.title, file.artist,
                    file.album, file.duration, file.contextualId));
        }
        return items;
    }
}
