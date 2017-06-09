package com.pdyjak.powerampwear.custom_views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class BlockingRecyclerView extends RecyclerView {

    private boolean mBlocked = false;

    public BlockingRecyclerView(Context context) {
        super(context);
    }

    public BlockingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBlocked(boolean blocked) {
        mBlocked = blocked;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mBlocked || super.dispatchTouchEvent(ev);
    }
}
