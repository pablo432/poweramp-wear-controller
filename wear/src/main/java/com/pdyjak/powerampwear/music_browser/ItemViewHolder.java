package com.pdyjak.powerampwear.music_browser;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ItemViewHolder extends RecyclerView.ViewHolder {
    public ItemViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindWith(@NonNull Object object);
}
