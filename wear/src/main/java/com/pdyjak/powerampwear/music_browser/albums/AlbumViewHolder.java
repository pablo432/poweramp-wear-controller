package com.pdyjak.powerampwear.music_browser.albums;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.music_browser.ItemViewHolder;

class AlbumViewHolder extends ItemViewHolder {
    @NonNull
    private final TextView mFirstLine;
    @NonNull
    private final TextView mSecondLine;
    private AlbumItem mCurrent;

    AlbumViewHolder(View itemView) {
        super(itemView);
        mFirstLine = (TextView) itemView.findViewById(R.id.first_line);
        mSecondLine = (TextView) itemView.findViewById(R.id.second_line);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrent == null) throw new IllegalStateException("Should never happen");
                mCurrent.onClicked();
            }
        });
    }

    @Override
    public void bindWith(@NonNull Object object) {
        mCurrent = (AlbumItem) object;
        mFirstLine.setText(mCurrent.name);
        mSecondLine.setText(mCurrent.artist);
    }
}
