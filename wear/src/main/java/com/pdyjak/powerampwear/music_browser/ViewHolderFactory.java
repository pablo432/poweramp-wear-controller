package com.pdyjak.powerampwear.music_browser;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

public interface ViewHolderFactory {
    @NonNull
    ItemViewHolder createViewHolder(ViewGroup parent);
}
