package com.github.quadflask.colorpicker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.quadflask.colorpicker.Color;
import com.github.quadflask.colorpicker.ColorCircle;
import com.github.quadflask.colorpicker.ColorSource;
import com.github.quadflask.colorpicker.OnColorChangedListener;
import com.github.quadflask.colorpicker.PaintBuilder;
import com.github.quadflask.colorpicker.renderer.ColorWheelRenderer;

public class ColorCursorView extends View implements OnColorChangedListener {
    private static final int TRANSPARENT = 0;

    private ColorWheelRenderer renderer;
    private ColorSource colorSource;
    private ColorCircle colorCircle;
    private boolean dirty = true;

    private Paint alphaPatternPaint = PaintBuilder.newPaint().build();
    private Paint colorWheelFill = PaintBuilder.newPaint().color(0).build();

    private Paint selectorStroke1 = PaintBuilder.newPaint().color(0xffffffff).build();
    private Paint selectorStroke2 = PaintBuilder.newPaint().color(0xff000000).build();

    final float STROKE_RATIO = 1.5f;

    public ColorCursorView(Context context) {
        super(context);
    }

    public ColorCursorView initWith(ColorWheelRenderer renderer, ColorSource colorSource) {
        this.renderer = renderer;
        this.colorSource = colorSource;
        this.colorSource.addListener(this);

        alphaPatternPaint.setShader(PaintBuilder.createAlphaPatternShader(26));

        return this;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ColorCircle colorCircle = renderer.findNearestByPosition(event.getX(), event.getY());

        if (colorCircle != null) {
            if (!colorCircle.equals(this.colorCircle)) {
                this.colorCircle = colorCircle;
                colorSource.updateColor(colorCircle.getColor());
                dirty = true;
                invalidate();
            }
        }

        return true;
    }

    @Override
    public void onColorChanged(Color color) {
        dirty = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (dirty) {
            canvas.drawColor(TRANSPARENT);

            if (colorCircle != null) {
                final float size = colorCircle.getR();

                colorWheelFill.setColor(android.graphics.Color.HSVToColor(colorCircle.getHsv()));
                colorWheelFill.setAlpha(colorCircle.getColor() >> 24);

                canvas.drawCircle(colorCircle.getX(), colorCircle.getY(), size * STROKE_RATIO, selectorStroke1);
                canvas.drawCircle(colorCircle.getX(), colorCircle.getY(), size * (1 + (STROKE_RATIO - 1) / 2), selectorStroke2);

                canvas.drawCircle(colorCircle.getX(), colorCircle.getY(), size, alphaPatternPaint);
                canvas.drawCircle(colorCircle.getX(), colorCircle.getY(), size, colorWheelFill);
                Log.e("ColorCursorView.onDraw", "render color cursor");
            }

            this.dirty = false;
        }
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
}
