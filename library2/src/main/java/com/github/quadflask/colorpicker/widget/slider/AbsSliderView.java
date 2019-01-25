package com.github.quadflask.colorpicker.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.quadflask.colorpicker.ColorSource;

public abstract class AbsSliderView extends View {
    protected static final int TRANSPARENT = 0;

    protected ColorSource colorSource;
    protected boolean dirty = true;
    protected float value = 1f;
    protected int width = 10;
    protected int height = 10;
    protected float handleRadius = 10;

    public AbsSliderView(Context context) {
        this(context, null);
    }

    public AbsSliderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (dirty) {
            canvas.drawColor(TRANSPARENT);

            handleRadius = getHeight() / 4f;
            width = getWidth();
            height = getHeight();

            drawBar(canvas, colorSource.getARGB());
            drawHandle(canvas, colorSource.getARGB());

            dirty = false;
        }
    }

    protected abstract void drawBar(Canvas canvas, int color);

    protected abstract void drawHandle(Canvas canvas, int color);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP: {
                value = Math.max(0, Math.min((event.getX() - handleRadius) / (width - handleRadius * 2), 1));
                colorSource.updateAlpha(value);
                dirty = true;
                invalidate();
                break;
            }
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        dirty = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        dirty = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = 0;
        if (widthMode == MeasureSpec.UNSPECIFIED)
            width = widthMeasureSpec;
        else if (widthMode == MeasureSpec.AT_MOST)
            width = MeasureSpec.getSize(widthMeasureSpec);
        else if (widthMode == MeasureSpec.EXACTLY)
            width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = 0;
        if (heightMode == MeasureSpec.UNSPECIFIED)
            height = heightMeasureSpec;
        else if (heightMode == MeasureSpec.AT_MOST)
            height = MeasureSpec.getSize(heightMeasureSpec);
        else if (heightMode == MeasureSpec.EXACTLY)
            height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }
}
