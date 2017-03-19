package com.pdyjak.powerampwear.music_browser.categories;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.music_browser.ItemViewHolder;

class CategoryViewHolder extends ItemViewHolder {

    @NonNull
    private final ImageView mIcon;
    @NonNull
    private final TextView mName;
    private CategoryItem mCurrent;

    CategoryViewHolder(View itemView) {
        super(itemView);
        mIcon = (ImageView) itemView.findViewById(R.id.icon);
        mName = (TextView) itemView.findViewById(R.id.name);
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
        mCurrent = (CategoryItem) object;
        mIcon.setImageResource(mCurrent.iconId);
        mName.setText(itemView.getContext().getText(mCurrent.stringId));
    }
}
