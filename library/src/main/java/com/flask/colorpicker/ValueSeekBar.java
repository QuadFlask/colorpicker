package com.flask.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ValueSeekBar extends View {
	private Bitmap bar;
	private int color;
	private int backgroundColor = 0x00000000;

	public ValueSeekBar(Context context) {
		super(context);
	}

	public ValueSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ValueSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		drawBar(getWidth(), getHeight());
	}

	private void drawBar(int width, int height) {
		if (bar == null)
			bar = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bar);

		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		Paint paint = PaintBuilder.newPaint().build();
		for (int x = 0; x < width; x++) {
			hsv[2] = (float) x / (width - 1);
			paint.setColor(Color.HSVToColor(hsv));
			canvas.drawLine(x, 0, x, height, paint);
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

				invalidate();
				break;
			}
			case MotionEvent.ACTION_UP: {

			}
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(backgroundColor);
		if (bar != null)
			canvas.drawBitmap(bar, 0, 0, null);
	}

	public void setColor(int color) {
		this.color = color;
	}
}