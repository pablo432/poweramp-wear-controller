package com.pdyjak.powerampwear.custom_views;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class SmoothScrollingLinearLayoutManager extends LinearLayoutManager {

    private static final float MILLIS_PER_INCH = 400f;

    private class SmoothScroller extends LinearSmoothScroller {

        private SmoothScroller(Context context) {
            super(context);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLIS_PER_INCH / displayMetrics.densityDpi;
        }

        @Nullable
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return SmoothScrollingLinearLayoutManager.this
                    .computeScrollVectorForPosition(targetPosition);
        }
    }

    private SmoothScroller mSmoothScroller;

    public SmoothScrollingLinearLayoutManager(Context context) {
        super(context);
        init(context);
    }

    public SmoothScrollingLinearLayoutManager(Context context, int orientation,
            boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init(context);
    }

    public SmoothScrollingLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mSmoothScroller = new SmoothScroller(context);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
            int position) {
        mSmoothScroller.setTargetPosition(position);
        startSmoothScroll(mSmoothScroller);
    }
}
