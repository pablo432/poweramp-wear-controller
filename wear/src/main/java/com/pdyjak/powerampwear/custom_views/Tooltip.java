package com.pdyjak.powerampwear.custom_views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class Tooltip extends TextView {

    private static final int STROKE_WIDTH = dpToPx(4);
    private static final int MARGIN = dpToPx(4);
    private static final int TOOLTIP_SIZE = dpToPx(8);
    private static final int ROUND_RADIUS = dpToPx(2);

    private final Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Tooltip(Context context) {
        this(context, null);
    }

    public Tooltip(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Tooltip(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFillPaint.setColor(0xFFAF7A6D);
        mFillPaint.setStyle(Paint.Style.FILL);
        mStrokePaint.setColor(0xFFE2D4BA);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int width = getWidth();
        final int height = getHeight();

        drawStuff(canvas, width, height, mStrokePaint);
        drawStuff(canvas, width, height, mFillPaint);
        super.onDraw(canvas);
    }

    private void drawStuff(Canvas canvas, int width, int height, Paint paint) {
        canvas.drawRoundRect(0 + MARGIN, 0 + MARGIN, width - MARGIN, height - TOOLTIP_SIZE - MARGIN,
                ROUND_RADIUS, ROUND_RADIUS, paint);
        Path p = new Path();
        p.moveTo(0 + MARGIN + width / 2 - TOOLTIP_SIZE / 2, height - TOOLTIP_SIZE - MARGIN);
        p.rLineTo(TOOLTIP_SIZE / 2, TOOLTIP_SIZE);
        p.rLineTo(TOOLTIP_SIZE / 2, -TOOLTIP_SIZE);
        p.close();
        canvas.drawPath(p, paint);
    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
