package com.pdyjak.powerampwear.music_browser;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.wearable.view.CurvedChildLayoutManager;
import android.support.wearable.view.ProgressSpinner;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdyjak.powerampwear.App;
import com.pdyjak.powerampwear.MessageExchangeHelper;
import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.settings.SettingsManager;

import java.util.List;

public abstract class BrowserFragmentBase extends Fragment {

    private class SettingsListener extends SettingsManager.Listener {
        @Override
        public void onCircularScrollingChanged() {
            if (mContentView == null) return;
            mContentView.setCircularScrollingGestureEnabled(
                    getSettingsManager().useCircularScrollingGesture());
        }
    };

    private class CacheInvalidationListener implements MusicLibraryCache.InvalidationListener {
        @Override
        public void onCacheInvalidated() {
            mSpinner.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
            fetchItems();
        }
    }

    @NonNull
    protected abstract ViewHolderFactory createViewHolderFactory();

    /**
     * Note: implementation, in case of success, should call setItems on base class before
     * returning true.
     * @return true, if items has been restored from cache
     */
    protected abstract boolean tryRestoreCachedItems();

    protected abstract void fetchItems();

    @NonNull
    private final BrowserRVAdapter<Clickable> mAdapter =
            new BrowserRVAdapter<>(createViewHolderFactory());
    @NonNull
    private final ScrollStateHelper mScrollStateHelper = new ScrollStateHelper();
    @NonNull
    private final SettingsListener mSettingsListener = new SettingsListener();
    @NonNull
    private final CacheInvalidationListener mCacheInvalidationListener =
            new CacheInvalidationListener();


    private WearableRecyclerView mContentView;
    @SuppressWarnings("deprecation") // Oh, come on, it's really nice
    private ProgressSpinner mSpinner;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.browser_view, container, false);
        mContentView = (WearableRecyclerView) view.findViewById(R.id.recycler_view);
        mContentView.setLayoutManager(new CurvedChildLayoutManager(getActivity()));
        mContentView.setCenterEdgeItems(true);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mContentView);
        //noinspection deprecation
        mSpinner = (ProgressSpinner) view.findViewById(R.id.spinner);
        mContentView.setAdapter(mAdapter);
        return view;
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        SettingsManager settingsManager = getSettingsManager();
        mContentView.setCircularScrollingGestureEnabled(
                settingsManager.useCircularScrollingGesture());
        settingsManager.addSettingsListener(mSettingsListener);
        getMusicLibraryCache().addInvalidationListener(mCacheInvalidationListener);
        if (tryRestoreCachedItems()) {
            mSpinner.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
            mScrollStateHelper.restoreTo(mContentView);
        } else {
            mSpinner.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
            fetchItems();
        }
    }

    @Override
    @CallSuper
    public void onPause() {
        super.onPause();
        getSettingsManager().removeListener(mSettingsListener);
        getMusicLibraryCache().removeInvalidationListener(mCacheInvalidationListener);
        mScrollStateHelper.save(mContentView);
    }

    protected final void setItems(@NonNull List<Clickable> items) {
        mSpinner.setVisibility(View.GONE);
        mContentView.setVisibility(View.VISIBLE);
        mAdapter.setItems(items);
    }

    @NonNull
    private App getApp() {
        return (App) getActivity().getApplicationContext();
    }

    @NonNull
    protected MessageExchangeHelper getMessageExchangeHelper() {
        return getApp().getMessageExchangeHelper();
    }

    @NonNull
    protected MusicLibraryCache getMusicLibraryCache() {
        return getApp().getCache();
    }

    @NonNull
    protected MusicLibraryNavigator getMusicLibraryNavigator() {
        return getApp().getMusicLibraryNavigator();
    }

    @NonNull
    protected SettingsManager getSettingsManager() {
        return getApp().getSettingsManager();
    }
}