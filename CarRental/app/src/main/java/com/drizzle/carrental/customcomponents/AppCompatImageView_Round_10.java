package com.drizzle.carrental.customcomponents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.drizzle.carrental.R;

public class AppCompatImageView_Round_10 extends AppCompatImageViewEx {

    public AppCompatImageView_Round_10(Context context) {
        this(context, null);
    }

    public AppCompatImageView_Round_10(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppCompatImageView_Round_10(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        final int scrollX = getScrollX();
        final int scrollY = getScrollY();

        if (clipPath != null)
            clipPath.reset();
        else
            clipPath = new Path();

        int radius = getResources().getDimensionPixelOffset(R.dimen.view_round_radius_10);
        clipPath.addRoundRect(new RectF(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                scrollX + getRight() - getLeft() - getPaddingRight(),
                scrollY + getBottom() - getTop() - getPaddingBottom()), radius, radius, Path.Direction.CW);

        super.onDraw(canvas);
    }
}
