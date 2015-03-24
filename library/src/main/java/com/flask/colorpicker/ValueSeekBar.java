package com.flask.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ValueSeekBar extends View {
	private Bitmap bar;
	private int color;

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
			hsv[2] = (float) x / width;
			paint.setColor(Color.HSVToColor(hsv));
			canvas.drawLine(x, 0, x, height, paint);
		}
	}
}