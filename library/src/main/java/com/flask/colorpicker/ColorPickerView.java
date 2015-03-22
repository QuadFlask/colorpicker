package com.flask.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

public class ColorPickerView extends View {
	private static final float STROKE_WIDTH = 2f;
	private static final float GAP_PERCENTAGE = 0.025f;

	private float COLOR_CIRCLE_MIN_R = 10;
	private float COLOR_CIRCLE_MAX_R = 20;

	private Bitmap colorWheel;

	private float value = 1;

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
		drawColorWheel(getWidth());
	}

	private void drawColorWheel(int width) {
		if (colorWheel == null)
			colorWheel = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);

		float c = width / 2f;
		Canvas canvas = new Canvas(colorWheel);
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);

		float x, y;
		float[] hsv = new float[3];
		Paint solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		int count = 10;
		float maxRadius = c - STROKE_WIDTH * 2 * (1f + GAP_PERCENTAGE);
		float sizeJitter = 0.0f;
		float cSize = maxRadius / count / 2;
		for (int i = 0; i < count; i++) {
			float p = (float) i / count; // 0~1
			float jitter = sizeJitter * (i - count / 2f) / count; // -0.5 ~ 0.5
			float radius = maxRadius * p;
			float size = cSize + cSize * jitter;
			int total = calcTotalCount(radius, size);
			for (int j = 0; j < total; j++) {
				float angle = (float) (Math.PI * 2 * j / total + (Math.PI / total) * (i % 2));
				x = c + (float) (radius * Math.cos(angle));
				y = c + (float) (radius * Math.sin(angle));
				hsv[0] = (float) (angle / Math.PI * 180);
				hsv[1] = radius / maxRadius;
				hsv[2] = value;
				solidPaint.setColor(Color.HSVToColor(hsv));

				canvas.drawCircle(x, y, size - STROKE_WIDTH * 2 * (1f + GAP_PERCENTAGE), solidPaint);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE: {
				invalidate();
			}
		}
		return true;
	}

	protected int calcTotalCount(float radius, float size) {
		return Math.max(1, (int) ((1f - GAP_PERCENTAGE) * Math.PI / (Math.asin(size / radius)) + 0.5f));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(0xff000000);
		canvas.drawBitmap(colorWheel, 0, 0, null);
	}
}