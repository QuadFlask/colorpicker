package com.github.quadflask.colorpicker.widget.wheel;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.github.quadflask.colorpicker.Color;
import com.github.quadflask.colorpicker.ColorCircle;
import com.github.quadflask.colorpicker.ColorSource;
import com.github.quadflask.colorpicker.OnColorChangedListener;
import com.github.quadflask.colorpicker.renderer.ColorWheelRenderer;

import java.util.List;

public class WheelView extends View implements OnColorChangedListener {
    private static final int TRANSPARENT = 0;

    private ColorWheelRenderer renderer;
    private ColorSource colorSource;
    private boolean dirty = true;

    public WheelView(Context context) {
        super(context);
    }

    public WheelView initWith(ColorWheelRenderer renderer, ColorSource colorSource) {
        this.renderer = renderer;
        this.colorSource = colorSource;
        this.colorSource.addListener(this);
        return this;
    }

    @Override
    public void onColorChanged(Color color) {
        dirty = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (dirty) {
            canvas.drawColor(TRANSPARENT);
            renderer.render(canvas, colorSource.getColor());

            Log.e("ColorCursorView.onDraw", "render color wheel");

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
