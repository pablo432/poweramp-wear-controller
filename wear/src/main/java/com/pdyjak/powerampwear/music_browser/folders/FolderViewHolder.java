package com.pdyjak.powerampwear.music_browser.folders;

import android.support.annotation.NonNull;
import android.view.View;

import com.pdyjak.powerampwear.music_browser.SimpleTwoLineItemViewHolderBase;

class FolderViewHolder extends SimpleTwoLineItemViewHolderBase {
    FolderViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindWith(@NonNull Object object) {
        super.bindWith(object);
        FolderItem item = (FolderItem) object;
        mFirstLine.setText(item.name);
        mSecondLine.setText(item.parentName);
    }
}
