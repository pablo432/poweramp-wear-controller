package com.pdyjak.powerampwear.music_browser;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ScrollStateHelper {
    private Parcelable mState;

    public void save(@Nullable RecyclerView recyclerView) {
        if (recyclerView == null) return;
        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        mState = lm.onSaveInstanceState();
    }

    public void restoreTo(@Nullable final RecyclerView recyclerView) {
        if (recyclerView == null || mState == null) return;
        final Parcelable state = mState;
        mState = null;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.getLayoutManager().onRestoreInstanceState(state);
            }
        });
    }
}
