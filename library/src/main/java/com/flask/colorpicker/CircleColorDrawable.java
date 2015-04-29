package com.flask.colorpicker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.flask.colorpicker.builder.PaintBuilder;

public class CircleColorDrawable extends BitmapDrawable {
	private int color;
	private float strokeWidth = 3f;
	private Paint strokePaint = PaintBuilder.newPaint().style(Paint.Style.STROKE).stroke(strokeWidth).color(0xff000000).build();
	private Paint fillPaint = PaintBuilder.newPaint().style(Paint.Style.FILL).color(0).build();
	private Paint fillBackPaint = PaintBuilder.newPaint().shader(PaintBuilder.createAlphaPatternShader((int) strokeWidth * 2)).build();

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		int intrinsicWidth = getIntrinsicWidth();
		float radius = intrinsicWidth / 2f;

		canvas.drawCircle(radius, radius, radius - strokeWidth / 2, fillBackPaint);
		canvas.drawCircle(radius, radius, radius - strokeWidth / 2, fillPaint);
		canvas.drawCircle(radius, radius, radius - strokeWidth, strokePaint);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		this.fillPaint.setColor(color);
		invalidateSelf();
	}
}