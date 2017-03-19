package com.pdyjak.powerampwear.music_browser;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.pdyjak.powerampwear.R;

public class SimpleTwoLineItemViewHolderBase extends ItemViewHolder {
    @NonNull
    protected final TextView mFirstLine;
    @NonNull
    protected final TextView mSecondLine;

    private Clickable mCurrentItem;

    public SimpleTwoLineItemViewHolderBase(View itemView) {
        super(itemView);
        mFirstLine = (TextView) itemView.findViewById(R.id.first_line);
        mSecondLine = (TextView) itemView.findViewById(R.id.second_line);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentItem == null) throw new IllegalStateException("Should never happen");
                mCurrentItem.onClicked();
            }
        });
    }

    @Override
    @CallSuper
    public void bindWith(@NonNull Object object) {
        mCurrentItem = (Clickable) object;
    }
}
