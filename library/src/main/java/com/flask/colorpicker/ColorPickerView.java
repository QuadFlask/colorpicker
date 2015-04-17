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

import com.flask.colorpicker.builder.PaintBuilder;
import com.flask.colorpicker.renderer.ColorWheelRenderOption;
import com.flask.colorpicker.renderer.ColorWheelRenderer;
import com.flask.colorpicker.slider.AlphaSlider;
import com.flask.colorpicker.slider.LightnessSlider;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

public class ColorPickerView extends View {
	private static final float STROKE_RATIO = 2f;

	private Bitmap colorWheel;
	private Canvas colorWheelCanvas;
	private int density = 10;

	private float lightness = 1;
	private float alpha = 1;
	private int backgroundColor = 0x00000000;

	private Integer initialColor = null;
	private Paint colorWheelFill = PaintBuilder.newPaint().color(0).build();
	private Paint selectorStroke1 = PaintBuilder.newPaint().color(0xffffffff).build();
	private Paint selectorStroke2 = PaintBuilder.newPaint().color(0xff000000).build();
	private ColorCircle currentColorCircle;

	private ArrayList<OnColorSelectedListener> listeners = new ArrayList<OnColorSelectedListener>();
	private LightnessSlider lightnessSlider;
	private AlphaSlider alphaSlider;
	private MaterialEditText colorEdit;

	private ColorWheelRenderer renderer;

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
		drawColorWheel();
	}

	private void drawColorWheel() {
		colorWheelCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

		float half = colorWheelCanvas.getWidth() / 2f;
		float strokeWidth = STROKE_RATIO * (1f + ColorWheelRenderer.GAP_PERCENTAGE);
		float maxRadius = half - strokeWidth - half / density;
		float cSize = maxRadius / (density - 1) / 2;

		ColorWheelRenderOption colorWheelRenderOption = renderer.getRenderOption();
		colorWheelRenderOption.density = this.density;
		colorWheelRenderOption.maxRadius = maxRadius;
		colorWheelRenderOption.cSize = cSize;
		colorWheelRenderOption.strokeWidth = strokeWidth;
		colorWheelRenderOption.alpha = alpha;
		colorWheelRenderOption.lightness = lightness;
		colorWheelRenderOption.targetCanvas = colorWheelCanvas;

		renderer.initWith(colorWheelRenderOption);

		renderer.draw();

		if (initialColor != null) {
			currentColorCircle = findNearestByColor(initialColor);
			float[] hsv = new float[3];
			Color.colorToHSV(initialColor, hsv);
			currentColorCircle.set(currentColorCircle.getX(), currentColorCircle.getY(), hsv);
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
				if (lightnessSlider != null)
					lightnessSlider.setColor(getSelectedColor());
				if (alphaSlider != null)
					alphaSlider.setColor(getSelectedColor());
				invalidate();
				break;
			}
			case MotionEvent.ACTION_UP: {
				if (listeners != null) {
					for (OnColorSelectedListener listener : listeners) {
						try {
							listener.onColorSelected(getSelectedColor());
						} catch (Exception e) {
							//Squash individual listener exceptions
						}
					}
				}
				if (lightnessSlider != null)
					lightnessSlider.setColor(getSelectedColor());
				if (alphaSlider != null)
					alphaSlider.setColor(getSelectedColor());
				invalidate();
				break;
			}
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(backgroundColor);
		if (colorWheel != null)
			canvas.drawBitmap(colorWheel, 0, 0, null);
		if (currentColorCircle != null) {
			float maxRadius = canvas.getWidth() / 2f - STROKE_RATIO * (1f + ColorWheelRenderer.GAP_PERCENTAGE);
			float size = maxRadius / density / 2;
			colorWheelFill.setColor(Color.HSVToColor(currentColorCircle.getHsvWithLightness(this.lightness)));
			colorWheelFill.setAlpha((int) (alpha * 0xff));
			canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size * STROKE_RATIO, selectorStroke1);
			canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size * (1 + (STROKE_RATIO - 1) / 2), selectorStroke2);
			canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size, colorWheelFill);
		}
	}

	private ColorCircle findNearestByPosition(float x, float y) {
		ColorCircle near = null;
		double minDist = Double.MAX_VALUE;

		for (ColorCircle colorCircle : renderer.getColorCircleList()) {
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

		for (ColorCircle colorCircle : renderer.getColorCircleList()) {
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

	private int getAlphaValueAsInt() {
		return Math.round(this.alpha * 255);
	}

	public int getSelectedColor() {
		int color = 0;
		if (currentColorCircle != null)
			color = Color.HSVToColor(currentColorCircle.getHsvWithLightness(this.lightness));
		return getAlphaValueAsInt() << 24 | (0x00ffffff & color);
	}

	public void setInitialColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);

		this.alpha = (color >> 24 & 0xff) / 255f;
		this.initialColor = color;
		this.lightness = hsv[2];
		if (renderer.getColorCircleList() != null)
			currentColorCircle = findNearestByColor(initialColor);
	}

	public void setLightness(float lightness) {
		this.lightness = lightness;
		this.initialColor = Color.HSVToColor(getAlphaValueAsInt(), currentColorCircle.getHsvWithLightness(lightness));
		if (this.colorEdit != null)
			this.colorEdit.setText("#" + Integer.toHexString(this.initialColor).toUpperCase());
		updateColorWheel();
		invalidate();
	}

	public void setColor(int color) {
		setInitialColor(color);
		updateColorWheel();
		invalidate();
	}

	public void setAlphaValue(float alpha) {
		this.alpha = alpha;
		this.initialColor = Color.HSVToColor(getAlphaValueAsInt(), currentColorCircle.getHsvWithLightness(this.lightness));
		if (this.colorEdit != null)
			this.colorEdit.setText("#" + Integer.toHexString(this.initialColor).toUpperCase());
		updateColorWheel();
		invalidate();
	}

	public void addOnColorSelectedListener(OnColorSelectedListener listener) {
		this.listeners.add(listener);
	}

	public void setLightnessSlider(LightnessSlider lightnessSlider) {
		this.lightnessSlider = lightnessSlider;
		if (lightnessSlider != null)
			this.lightnessSlider.setColorPicker(this);
	}

	public void setAlphaSlider(AlphaSlider alphaSlider) {
		this.alphaSlider = alphaSlider;
		if (alphaSlider != null)
			this.alphaSlider.setColorPicker(this);
	}

	public void setColorEdit(MaterialEditText colorEdit) {
		this.colorEdit = colorEdit;
		if (this.colorEdit != null)
			this.colorEdit.setVisibility(View.VISIBLE);
	}

	public void setDensity(int density) {
		this.density = Math.max(2, density);
		invalidate();
	}

	public void setRenderer(ColorWheelRenderer renderer) {
		this.renderer = renderer;
		invalidate();
	}

	public enum WHEEL_TYPE {
		FLOWER, CIRCLE
	}
}