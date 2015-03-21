package com.flask.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

public class ColorPickerView extends View {
	private Bitmap colorWheel;

	public ColorPickerView(Context context) {
		super(context);
	}

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		updateColorWheel(getWidth());
	}

	private void updateColorWheel(int size) {
		if (colorWheel == null)
			colorWheel = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

		float c = size / 2f;
		Canvas canvas = new Canvas(colorWheel);
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		Paint solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		strokePaint.setColor(0xffff0000);

		float minR = 10;
		float maxR = 20;
		float x, y, r;
		float max_radius = c * 0.9f;
		float[] hsv = new float[3];

		for (float radius = 0; radius < max_radius; radius += r * 2) {
			r = minR + (maxR - minR) * radius / max_radius;
			int total = calcTotalCount(radius, r);
			for (int i = 0; i < total; i++) {
				float angle = (float) (Math.PI * 2 * i / total);
				x = c + (float) (radius * Math.cos(angle));
				y = c + (float) (radius * Math.sin(angle));
				hsv[0] = (float) (angle / Math.PI * 180);
				hsv[1] = radius / max_radius;
				hsv[2] = 1;
				solidPaint.setColor(Color.HSVToColor(hsv));
				canvas.drawCircle(x, y, r - STROKE_SIZE * 2 * (1f + GAP_PERCENTAGE), solidPaint);
			}
		}
	}

	private static final float STROKE_SIZE = 2f;
	private static final float GAP_PERCENTAGE = 0.025f;

	protected int calcTotalCount(float radius, float size) {
		return Math.max(1, (int) ((1f - GAP_PERCENTAGE) * Math.PI / (Math.asin(size / radius)) + 0.5f));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(colorWheel, 0, 0, null);
	}
}