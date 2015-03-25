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

import java.util.HashSet;
import java.util.Set;

public class ColorPickerView extends View {
	private static final float STROKE_RATIO = 2f;
	private static final float GAP_PERCENTAGE = 0.025f;

	private Bitmap colorWheel;
	private Canvas colorWheelCanvas;

	private int count = 8;
	private float half;
	private float value = 1;
	private int backgroundColor = 0x00000000;
	private Integer initialColor = null;

	private Paint colorWheelFill = PaintBuilder.newPaint().color(0).build();
	private Paint selectorStroke1 = PaintBuilder.newPaint().color(0xffffffff).build();
	private Paint selectorStroke2 = PaintBuilder.newPaint().color(0xff000000).build();
	private Paint selectorFill = PaintBuilder.newPaint().build();

	private Set<ColorCircle> colorCircleSet = new HashSet<>();
	private ColorCircle currentColorCircle;
	private OnColorSelectedListener listener;
	private LightnessBar lightnessBar;

	private float lastMx, lastMy;

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
		drawColorWheel(getWidth());
	}

	private void drawColorWheel(int width) {
		if (colorWheel == null) {
			colorWheel = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
			colorWheelCanvas = new Canvas(colorWheel);
		}

		colorWheelCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		colorCircleSet.clear();

		half = width / 2f;

		float x, y;
		float[] hsv = new float[3];
		float sizeJitter = 0.0f;
		float maxRadius = half - STROKE_RATIO * (1f + GAP_PERCENTAGE) - half / count;
		float cSize = maxRadius / (count - 1) / 2;

		for (int i = 0; i < count; i++) {
			float p = (float) i / (count - 1); // 0~1
			float jitter = sizeJitter * (i - count / 2f) / count; // -0.5 ~ 0.5
			float radius = maxRadius * p;
			float size = cSize + cSize * jitter;
			int total = calcTotalCount(radius, size);
			for (int j = 0; j < total; j++) {
				double angle = Math.PI * 2 * j / total + (Math.PI / total) * ((i + 1) % 2);
				x = half + (float) (radius * Math.cos(angle));
				y = half + (float) (radius * Math.sin(angle));
				hsv[0] = (float) (angle / Math.PI * 180);
				hsv[1] = radius / maxRadius;
				hsv[2] = value;
				selectorFill.setColor(Color.HSVToColor(hsv));

				colorWheelCanvas.drawCircle(x, y, size - STROKE_RATIO * (1f + GAP_PERCENTAGE), selectorFill);
				colorCircleSet.add(new ColorCircle(x, y, hsv));
			}
		}
		if (initialColor != null) {
			currentColorCircle = findNearestByColor(initialColor);
			initialColor = null;
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
				lastMx = event.getX();
				lastMy = event.getY();
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
			float size = maxRadius / count / 2;
			colorWheelFill.setColor(Color.HSVToColor(currentColorCircle.getHsv()));
			canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size * STROKE_RATIO, selectorStroke1);
			canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size * (1 + (STROKE_RATIO - 1) / 2), selectorStroke2);
			canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size, colorWheelFill);
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
		this.value = hsv[2];
		if (colorCircleSet != null)
			currentColorCircle = findNearestByColor(initialColor);
	}

	public void setValue(float v) {
		this.value = v;
		updateColorWheel();
		invalidate();
		lightnessBar.setColor(getSelectedColor());
		if (lastMx != 0)
			currentColorCircle = findNearestByPosition(lastMx, lastMy);
		else
			currentColorCircle = findNearestByColor(getSelectedColor());
	}

	public void setOnColorSelectedListener(OnColorSelectedListener listener) {
		this.listener = listener;
	}

	public void setLightnessBar(LightnessBar lightnessBar) {
		this.lightnessBar = lightnessBar;
		this.lightnessBar.setColorPicker(this);
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