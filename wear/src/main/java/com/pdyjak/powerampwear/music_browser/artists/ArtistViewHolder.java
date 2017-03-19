package com.pdyjak.powerampwear.music_browser.artists;

import android.support.annotation.NonNull;
import android.view.View;

import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.music_browser.SimpleTwoLineItemViewHolderBase;

public class ArtistViewHolder extends SimpleTwoLineItemViewHolderBase {
    public ArtistViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindWith(@NonNull Object object) {
        super.bindWith(object);
        ArtistItem item = (ArtistItem) object;
        mFirstLine.setText(item.name);
        mSecondLine.setText(itemView.getContext()
                .getString(R.string.songs_count_format, item.songsCount));
    }
}
