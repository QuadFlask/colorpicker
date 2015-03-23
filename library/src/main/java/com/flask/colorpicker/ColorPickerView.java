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

	private Bitmap colorWheel;

	private float value = 1;
	private float wheelRadius = 0;
	private Set<ColorCircle> colorCircleSet;
	private ColorCircle currentColorCircle;

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

		Canvas canvas = new Canvas(colorWheel);
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);

		Paint solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		colorCircleSet = new HashSet<>();

		float x, y;
		float c = width / 2f;
		float[] hsv = new float[3];
		int count = 10;
		float sizeJitter = 0.0f;
		float maxRadius = c - STROKE_WIDTH * 2 * (1f + GAP_PERCENTAGE);
		float cSize = maxRadius / count / 2;
		wheelRadius = maxRadius;

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
				colorCircleSet.add(new ColorCircle(x, y, hsv));
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
		if (currentColorCircle != null) {

		}
	}

	private ColorCircle findNearestByPosition(float x, float y) {
		ColorCircle near = null;
		double minDist = Double.MAX_VALUE;

		for (ColorCircle colorCircle : colorCircleSet) {
			double dist = colorCircle.sqDist(x, y);
			if (minDist > dist) {
				minDist = dist;
				near = colorCircle;
			}
		}

		return near;
	}

	private ColorCircle findNearestByColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		ColorCircle near = null;
		double minDiff = Double.MAX_VALUE;
		double x = hsv[1] * Math.cos(hsv[0] / 180 * Math.PI);
		double y = hsv[1] * Math.sin(hsv[0] / 180 * Math.PI);

		for (ColorCircle colorCircle : colorCircleSet) {
			float[] hsv1 = colorCircle.getHsv();
			double x1 = hsv1[1] * Math.cos(hsv1[0] / 180 * Math.PI);
			double y1 = hsv1[1] * Math.sin(hsv1[0] / 180 * Math.PI);
			double dx = x - x1;
			double dy = y - y1;
			double dist = dx * dx + dy * dy;
			if (dist < minDiff) {
				minDiff = dist;
				near = colorCircle;
			}
		}

		return near;
	}
}

class ColorCircle {
	private float x, y;
	private float[] hsv = new float[3];

	ColorCircle(float x, float y, float[] hsv) {
		this.x = x;
		this.y = y;
		this.hsv[0] = hsv[0];
		this.hsv[1] = hsv[1];
		this.hsv[2] = hsv[2];
	}

	public double sqDist(float x, float y) {
		double dx = this.x - x;
		double dy = this.y - y;
		return dx * dx + dy * dy;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float[] getHsv() {
		return hsv;
	}
}