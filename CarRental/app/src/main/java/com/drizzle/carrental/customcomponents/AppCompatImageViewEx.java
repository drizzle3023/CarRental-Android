package com.drizzle.carrental.customcomponents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class AppCompatImageViewEx extends AppCompatImageView {

    protected Drawable backgroundDrawable;
    protected boolean drawBackgroundDrawable = false;
    protected boolean useClipPath = true;
    protected Path clipPath = null;
    protected boolean drawStroke = false;
    protected int strokeWidth = 5;
    protected int strokeColor = 0xFF00FF00;

    public AppCompatImageViewEx(Context context) {
        this(context, null);
    }

    public AppCompatImageViewEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppCompatImageViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setDrawingCacheEnabled(false);
        setWillNotCacheDrawing(false);
    }

    @Override
    public void setBackgroundDrawable(Drawable drawable) {
        this.backgroundDrawable = drawable;
        super.setBackgroundDrawable(drawable);
    }

    public void setDrawBackgroundDrawable(boolean drawBackgroundDrawable) {
        this.drawBackgroundDrawable = drawBackgroundDrawable;
        invalidate();
    }

    public void setClipPath(Path clipPath) {
        this.clipPath = clipPath;
    }

    public void setDrawStroke(boolean drawStroke) {
        this.drawStroke = drawStroke;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    @Override
    public void onDraw(Canvas canvas) {

        int saveCount = canvas.getSaveCount();
        canvas.save();

        if (useClipPath && clipPath != null)
            canvas.clipPath(clipPath);

        super.onDraw(canvas);

        if (drawStroke && clipPath != null) {
            Paint paint = new Paint();
            paint.setStrokeWidth(strokeWidth);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(strokeColor);
            canvas.drawPath(clipPath, paint);
        }

        canvas.restoreToCount(saveCount);
    }
}
