package com.flask.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerView extends View {
	public enum WHEEL_TYPE {
		FLOWER, CIRCLE
	}

	private static final float STROKE_RATIO = 2f;
	private static final float GAP_PERCENTAGE = 0.025f;

	private Bitmap colorWheel;

	private Canvas colorWheelCanvas;
	private WHEEL_TYPE wheelType = WHEEL_TYPE.CIRCLE;
	private int density = 10;

	private float half;
	private float lightness = 1;
	private int backgroundColor = 0x00000000;
	private Integer initialColor = null;

	private Paint colorWheelFill = PaintBuilder.newPaint().color(0).build();
	private Paint selectorStroke1 = PaintBuilder.newPaint().color(0xffffffff).build();
	private Paint selectorStroke2 = PaintBuilder.newPaint().color(0xff000000).build();
	private Paint selectorFill = PaintBuilder.newPaint().build();

	private List<ColorCircle> colorCircleList = new ArrayList<>(128);
	private ColorCircle currentColorCircle;
	private OnColorSelectedListener listener;

	private LightnessBar lightnessBar;

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
		updateColorWheel();
	}

	private void updateColorWheel() {
		int width = getWidth();
		if (colorWheel == null) {
			colorWheel = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
			colorWheelCanvas = new Canvas(colorWheel);
		}
		drawColorWheel(width);
	}

	private void drawColorWheel(int width) {
		colorWheelCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

		half = width / 2f;

		float maxRadius = half - STROKE_RATIO * (1f + GAP_PERCENTAGE) - half / density;
		float cSize = maxRadius / (density - 1) / 2;

		if (wheelType == WHEEL_TYPE.CIRCLE)
			drawCircleColorWheel(maxRadius, cSize);
		else if (wheelType == WHEEL_TYPE.FLOWER)
			drawFlowerColorWheel(maxRadius, cSize);

		if (initialColor != null) {
			currentColorCircle = findNearestByColor(initialColor);
			initialColor = null;
		}
	}

	private void drawCircleColorWheel(float maxRadius, float cSize) {
		float x, y;
		float[] hsv = new float[3];
		float sizeJitter = 0.0f;
		final int setSize = colorCircleList.size();
		int currentCount = 0;

		for (int i = 0; i < density; i++) {
			float p = (float) i / (density - 1); // 0~1
			float jitter = sizeJitter * (i - density / 2f) / density; // -0.5 ~ 0.5
			float radius = maxRadius * p;
			float size = cSize + cSize * jitter;
			int total = calcTotalCount(radius, size);
			for (int j = 0; j < total; j++) {
				double angle = Math.PI * 2 * j / total + (Math.PI / total) * ((i + 1) % 2);
				x = half + (float) (radius * Math.cos(angle));
				y = half + (float) (radius * Math.sin(angle));
				hsv[0] = (float) (angle / Math.PI * 180);
				hsv[1] = radius / maxRadius;
				hsv[2] = lightness;
				selectorFill.setColor(Color.HSVToColor(hsv));

				colorWheelCanvas.drawCircle(x, y, size - STROKE_RATIO * (1f + GAP_PERCENTAGE), selectorFill);

				if (currentCount >= setSize)
					colorCircleList.add(new ColorCircle(x, y, hsv));
				else colorCircleList.get(currentCount).set(x, y, hsv);
				currentCount++;
			}
		}
	}

	private void drawFlowerColorWheel(float maxRadius, float cSize) {
		float x, y;
		float[] hsv = new float[3];
		float sizeJitter = 1.2f;
		final int setSize = colorCircleList.size();
		float stroke = STROKE_RATIO * (1f + GAP_PERCENTAGE);
		int currentCount = 0;

		for (int i = 0; i < density; i++) {
			float jitter = (i - density / 2f) / density; // -0.5 ~ 0.5
			float p = (float) i / (density - 1); // 0~1
			float radius = maxRadius * p;
			float size = Math.max(1.5f + stroke, cSize + (i == 0 ? 0 : cSize * sizeJitter * jitter));
			int total = Math.min(calcTotalCount(radius, size), density * 2);
			for (int j = 0; j < total; j++) {
				double angle = Math.PI * 2 * j / total + (Math.PI / total) * ((i + 1) % 2);
				x = half + (float) (radius * Math.cos(angle));
				y = half + (float) (radius * Math.sin(angle));
				hsv[0] = (float) (angle / Math.PI * 180);
				hsv[1] = radius / maxRadius;
				hsv[2] = lightness;
				selectorFill.setColor(Color.HSVToColor(hsv));

				colorWheelCanvas.drawCircle(x, y, size - stroke, selectorFill);

				if (currentCount >= setSize)
					colorCircleList.add(new ColorCircle(x, y, hsv));
				else colorCircleList.get(currentCount).set(x, y, hsv);
				currentCount++;
			}
		}
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
		setMeasuredDimension(width, width);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE: {
				currentColorCircle = findNearestByPosition(event.getX(), event.getY());
				lightnessBar.setColor(getSelectedColor());
				invalidate();
				break;
			}
			case MotionEvent.ACTION_UP: {
				if (listener != null) listener.onColorSelected(getSelectedColor());
				lightnessBar.setColor(getSelectedColor());
				invalidate();
				break;
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
		canvas.drawColor(backgroundColor);
		if (colorWheel != null)
			canvas.drawBitmap(colorWheel, 0, 0, null);
		if (currentColorCircle != null) {
			float maxRadius = half - STROKE_RATIO * (1f + GAP_PERCENTAGE);
			float size = maxRadius / density / 2;
			colorWheelFill.setColor(Color.HSVToColor(currentColorCircle.getHsv()));
			canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size * STROKE_RATIO, selectorStroke1);
			canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size * (1 + (STROKE_RATIO - 1) / 2), selectorStroke2);
			canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size, colorWheelFill);
		}
	}

	private ColorCircle findNearestByPosition(float x, float y) {
		ColorCircle near = null;
		double minDist = Double.MAX_VALUE;

		for (ColorCircle colorCircle : colorCircleList) {
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

		for (ColorCircle colorCircle : colorCircleList) {
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

	public int getSelectedColor() {
		int color = 0;
		if (currentColorCircle != null)
			color = Color.HSVToColor(currentColorCircle.getHsv());
		return color;
	}

	public void setInitialColor(int color) {
		this.initialColor = color;
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		this.lightness = hsv[2];
		if (colorCircleList != null)
			currentColorCircle = findNearestByColor(initialColor);
	}

	public void setLightness(float lightness) {
		this.lightness = lightness;
		updateColorWheel();
		invalidate();
		lightnessBar.setColor(getSelectedColor());
	}

	public void setOnColorSelectedListener(OnColorSelectedListener listener) {
		this.listener = listener;
	}

	public void setLightnessBar(LightnessBar lightnessBar) {
		this.lightnessBar = lightnessBar;
		this.lightnessBar.setColorPicker(this);
	}

	public void setWheelType(WHEEL_TYPE wheelType) {
		this.wheelType = wheelType;
		invalidate();
	}

	public void setDensity(int density) {
		this.density = Math.max(2, density);
		invalidate();
	}
}

class ColorCircle {
	private float x, y;
	private float[] hsv = new float[3];

	ColorCircle(float x, float y, float[] hsv) {
		set(x, y, hsv);
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

	public void set(float x, float y, float[] hsv) {
		this.x = x;
		this.y = y;
		this.hsv[0] = hsv[0];
		this.hsv[1] = hsv[1];
		this.hsv[2] = hsv[2];
	}
}

class PaintBuilder {
	public static PaintHolder newPaint() {
		return new PaintHolder();
	}

	public static class PaintHolder {
		private Paint paint;

		private PaintHolder() {
			this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}

		public PaintHolder color(int color) {
			this.paint.setColor(color);
			return this;
		}

		public PaintHolder antiAlias(boolean flag) {
			this.paint.setAntiAlias(flag);
			return this;
		}

		public PaintHolder style(Paint.Style style) {
			this.paint.setStyle(style);
			return this;
		}

		public PaintHolder mode(PorterDuff.Mode mode) {
			this.paint.setXfermode(new PorterDuffXfermode(mode));
			return this;
		}

		public PaintHolder stroke(float width) {
			this.paint.setStrokeWidth(width);
			return this;
		}

		public Paint build() {
			return this.paint;
		}
	}
}