package com.pdyjak.powerampwear.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.pdyjak.powerampwear.R;

public class CircularProgressbar extends View {

    private final RectF mRect = new RectF();
    private final Paint mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mFgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float mHalfThickness;
    private float mProgress;

    public CircularProgressbar(Context context) {
        this(context, null);
    }

    public CircularProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mFgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setColor(ContextCompat.getColor(context, R.color.circle_progressbar_default_bg));
        mFgPaint.setColor(ContextCompat.getColor(context, R.color.circle_progressbar_default_fg));
        final float thickness = context.getResources()
                .getDimensionPixelSize(R.dimen.circle_seekbar_default_thickness);
        setThickness(thickness);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRect.set(mHalfThickness, mHalfThickness,
                getWidth() - mHalfThickness, getHeight() - mHalfThickness);
        canvas.drawArc(mRect, 0, 360, false, mBgPaint);
        canvas.drawArc(mRect, -90, mProgress * 360.f, false, mFgPaint);
    }

    public void setThickness(float thickness) {
        mHalfThickness = thickness / 2;
        mBgPaint.setStrokeWidth(thickness);
        mFgPaint.setStrokeWidth(thickness);
        invalidate();
    }

    public void setProgress(@FloatRange(from=0.f, to=1.f) float progress) {
        mProgress = progress;
        invalidate();
    }
}
