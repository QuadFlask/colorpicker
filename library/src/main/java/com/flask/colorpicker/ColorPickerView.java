package com.flask.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

	private ColorCircle nearestColorCircle;
	private Paint selectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint selectorLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private float value = 1;
	private float minR = 10;
	private float maxR = 20;
	private Set<ColorCircle> colorCircleSet;

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

		selectorLinePaint.setStyle(Paint.Style.STROKE);
		selectorLinePaint.setColor(0xff000000);
		selectorLinePaint.setStrokeWidth(STROKE_WIDTH);

		float c = size / 2f;
		Canvas canvas = new Canvas(colorWheel);
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		Paint solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		float x, y, r;
		float max_radius = c - STROKE_WIDTH * 2 * (1f + GAP_PERCENTAGE);
		float[] hsv = new float[3];

		colorCircleSet = new HashSet<>();

		for (float radius = 0; radius < max_radius; radius += r * 2) {
			r = minR + (maxR - minR) * radius / max_radius;
			int total = calcTotalCount(radius, r);
			for (int i = 0; i < total; i++) {
				float angle = (float) (Math.PI * 2 * i / total);
				x = c + (float) (radius * Math.cos(angle));
				y = c + (float) (radius * Math.sin(angle));
				hsv[0] = (float) (angle / Math.PI * 180);
				hsv[1] = radius / max_radius;
				hsv[2] = value;
				solidPaint.setColor(Color.HSVToColor(hsv));
				float radiusOfColorCircle = r - STROKE_WIDTH * 2 * (1f + GAP_PERCENTAGE);
				canvas.drawCircle(x, y, radiusOfColorCircle, solidPaint);

				colorCircleSet.add(new ColorCircle(x, y, radiusOfColorCircle, Color.HSVToColor(hsv)));
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE: {
				nearestColorCircle = findNearestColorCircle(event.getX(), event.getY());
				invalidate();
			}
		}
		return true;
	}

	public void setColorWheelValue(float value) {
		this.value = Math.max(0, Math.min(value, 1f));
	}

	protected int calcTotalCount(float radius, float size) {
		return Math.max(1, (int) ((1f - GAP_PERCENTAGE) * Math.PI / (Math.asin(size / radius)) + 0.5f));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(colorWheel, 0, 0, null);
		selectorPaint.setColor(nearestColorCircle.getColor());
		canvas.drawCircle(nearestColorCircle.getX(), nearestColorCircle.getY(), nearestColorCircle.getR() - selectorLinePaint.getStrokeWidth() * 2, selectorPaint);
		canvas.drawCircle(nearestColorCircle.getX(), nearestColorCircle.getY(), nearestColorCircle.getR() - selectorLinePaint.getStrokeWidth() * 2, selectorLinePaint);
	}

	private ColorCircle findNearestColorCircle(float x, float y) {
		ColorCircle nearestColorCircle = null;
		double currentNearestSqDist = Double.MIN_VALUE;

		for (ColorCircle c : colorCircleSet) {
			double sqDist = c.sqDist(x, y);
			if (sqDist < currentNearestSqDist) {
				nearestColorCircle = c;
				currentNearestSqDist = sqDist;
			}
		}

		return nearestColorCircle;
	}
}

class ColorCircle {
	private int color;
	private float x, y;
	private float r;

	public ColorCircle(float x, float y, float radiusOfColorCircle, int color) {
		this.x = x;
		this.y = y;
		this.r = radiusOfColorCircle;
		this.color = color;
	}

	public double sqDist(float x, float y) {
		double dx = this.x - x;
		double dy = this.y - y;
		return dx * dx + dy * dy;
	}

	public int getColor() {
		return color;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getR() {
		return r;
	}
}