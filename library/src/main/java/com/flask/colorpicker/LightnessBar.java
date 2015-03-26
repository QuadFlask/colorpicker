package com.flask.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LightnessBar extends View {
	private Bitmap bar;
	private Canvas barCanvas;
	private int barOffsetX;
	private int color;
	private int backgroundColor = 0x00000000;
	private int handleRadius = 20;
	private int barHeight = 5;
	private float lightness = 1;

	private Paint solid = PaintBuilder.newPaint().build();
	private Paint stroke1 = PaintBuilder.newPaint().color(0xffffffff).build();
	private Paint stroke2 = PaintBuilder.newPaint().color(0xff000000).build();
	private Paint barPaint = PaintBuilder.newPaint().build();

	private ColorPickerView colorPicker;

	public LightnessBar(Context context) {
		super(context);
	}

	public LightnessBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LightnessBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		updateBar();
	}

	private void updateBar() {
		handleRadius = getHeight() / 2;
		barHeight = getHeight() / 6;
		barOffsetX = handleRadius;
		drawBar(getWidth() - barOffsetX * 2, barHeight);
	}

	private void drawBar(int width, int height) {
		if (bar == null) {
			bar = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			barCanvas = new Canvas(bar);
		}

		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		int l = width / 256;
		for (int x = 0; x < width; x += l) {
			hsv[2] = (float) x / (width - 1);
			barPaint.setColor(Color.HSVToColor(hsv));
			barCanvas.drawRect(x, 0, x + l, height, barPaint);
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

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int height = 0;
		if (heightMode == MeasureSpec.UNSPECIFIED)
			height = heightMeasureSpec;
		else if (heightMode == MeasureSpec.AT_MOST)
			height = MeasureSpec.getSize(heightMeasureSpec);
		else if (heightMode == MeasureSpec.EXACTLY)
			height = MeasureSpec.getSize(heightMeasureSpec);

		setMeasuredDimension(width, height);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE: {
				lightness = (event.getX() - barOffsetX) / bar.getWidth();
				lightness = Math.max(0, Math.min(lightness, 1));
				colorPicker.setValue(lightness);
				invalidate();
				break;
			}
			case MotionEvent.ACTION_UP: {
				colorPicker.setValue(lightness);
				invalidate();
			}
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(backgroundColor);
		if (bar != null)
			canvas.drawBitmap(bar, barOffsetX, (getHeight() - bar.getHeight()) / 2, null);

		solid.setColor(colorAtLightness(color, lightness));
		float x = handleRadius + lightness * (getWidth() - handleRadius * 2);
		float y = getHeight() / 2f;

		canvas.drawCircle(x, y, handleRadius, stroke1);
		canvas.drawCircle(x, y, handleRadius * 0.8f, stroke2);
		canvas.drawCircle(x, y, handleRadius * 0.6f, solid);
	}

	public void setColor(int color) {
		this.color = color;
		lightness = lightnessOfColor(color);
		if (bar != null) {
			updateBar();
			invalidate();
		}
	}

	public float getLightness() {
		return lightness;
	}

	public void setColorPicker(ColorPickerView colorPicker) {
		this.colorPicker = colorPicker;
	}

	private int colorAtLightness(int color, float lightness) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] = lightness;
		return Color.HSVToColor(hsv);
	}

	private float lightnessOfColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		return hsv[2];
	}
}