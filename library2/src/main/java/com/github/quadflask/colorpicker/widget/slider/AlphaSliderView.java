package com.github.quadflask.colorpicker.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.quadflask.colorpicker.Color;
import com.github.quadflask.colorpicker.ColorSource;
import com.github.quadflask.colorpicker.OnColorChangedListener;
import com.github.quadflask.colorpicker.PaintBuilder;

public class AlphaSliderView extends View implements OnColorChangedListener {
    private static final int TRANSPARENT = 0;

    private ColorSource colorSource;

    private Paint alphaPatternPaint = PaintBuilder.newPaint().build();
    private Paint barPaint = PaintBuilder.newPaint().build();
    private Paint solid = PaintBuilder.newPaint().build();
    private Paint clearingStroke = PaintBuilder.newPaint().color(0xffffffff).xPerMode(PorterDuff.Mode.CLEAR).build();

    private float value = 1f;
    private float width = 10;
    private float height = 10;
    private float handleRadius = 10;

    private boolean dirty = true;

    public AlphaSliderView(Context context) {
        this(context, null);
    }

    public AlphaSliderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        alphaPatternPaint.setShader(PaintBuilder.createAlphaPatternShader(16));
    }

    public void setColorSource(ColorSource colorSource) {
        this.colorSource = colorSource;
        this.colorSource.addListener(this);
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

            this.dirty = false;
        }
    }

    private void drawBar(Canvas canvas, int color) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        int barW = (int) (w - handleRadius * 2);
        int barH = (int) (h / 10f);

        int ox = (int) handleRadius;
        int oy = (h - barH) / 2;
        int l = Math.max(2, barW / 256);

        canvas.drawRect(ox, oy, ox + barW, oy + barH, alphaPatternPaint);
        for (int x = 0; x < barW; x += l) {
            float alpha = (float) x / (barW - 1);
            barPaint.setColor(color);
            barPaint.setAlpha(Math.round(alpha * 255));
            canvas.drawRect(ox + x, oy, ox + x + l, oy + barH, barPaint);
        }
    }

    private void drawHandle(Canvas canvas, int color) {
        solid.setColor(color);
        solid.setAlpha(Math.round(value * 255));

        float x = handleRadius + value * (width - handleRadius * 2);
        float y = height / 2f;

        canvas.drawCircle(x, y, handleRadius * 1.33f, clearingStroke);
        if (value < 1)
            canvas.drawCircle(x, y, handleRadius, alphaPatternPaint);
        canvas.drawCircle(x, y, handleRadius, solid);
    }

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
    public void onColorChanged(Color color) {
        dirty = true;
        invalidate();
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
