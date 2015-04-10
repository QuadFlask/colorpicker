package com.flask.colorpicker.slider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.PaintBuilder;

public class AlphaSlider extends AbsCustomSlider {
	public int color;
	private Shader patternShader;
	private Paint alphaPatternPaint = PaintBuilder.newPaint().build();
	private Paint barPaint = PaintBuilder.newPaint().build();
	private Paint solid = PaintBuilder.newPaint().build();
	private Paint stroke1 = PaintBuilder.newPaint().color(0xffffffff).xPerMode(PorterDuff.Mode.CLEAR).build();

	private ColorPickerView colorPicker;

	public AlphaSlider(Context context) {
		super(context);
	}

	public AlphaSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlphaSlider(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void createBitmaps() {
		super.createBitmaps();
		patternShader = createAlphaPatternShader(barHeight / 2);
		alphaPatternPaint.setShader(patternShader);
	}

	@Override
	protected void drawBar(Canvas barCanvas) {
		int width = barCanvas.getWidth();
		int height = barCanvas.getHeight();

		barCanvas.drawRect(0, 0, width, height, alphaPatternPaint);
		int l = Math.max(2, width / 256);
		for (int x = 0; x <= width; x += l) {
			float alpha = (float) x / (width - 1);
			barPaint.setColor(color);
			barPaint.setAlpha(Math.round(alpha * 255));
			barCanvas.drawRect(x, 0, x + l, height, barPaint);
		}
	}

	@Override
	protected void onValueChanged(float value) {
		colorPicker.setAlphaValue(value);
	}

	@Override
	protected void drawHandle(Canvas canvas, float x, float y) {
		solid.setColor(color);
		solid.setAlpha(Math.round(value * 255));
		canvas.drawCircle(x, y, handleRadius, stroke1);
		canvas.drawCircle(x, y, handleRadius * 0.75f, alphaPatternPaint);
		canvas.drawCircle(x, y, handleRadius * 0.75f, solid);
	}

	private Shader createAlphaPatternShader(int size) {
		return new BitmapShader(createAlphaBackgroundPattern(size), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
	}

	private Bitmap createAlphaBackgroundPattern(int size) {
		size /= 2;
		size = Math.max(8, size * 2);
		Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		int s = Math.round(size / 2f);
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 2; j++) {
				if ((i + j) % 2 == 0) alphaPatternPaint.setColor(0xffffffff);
				else alphaPatternPaint.setColor(0xffd0d0d0);
				c.drawRect(i * s, j * s, (i + 1) * s, (j + 1) * s, alphaPatternPaint);
			}
		return bm;
	}

	public void setColorPicker(ColorPickerView colorPicker) {
		this.colorPicker = colorPicker;
	}

	public void setColor(int color) {
		this.color = color;
		this.value = alphaOfColor(color);
		if (bar != null) {
			updateBar();
			invalidate();
		}
	}

	private float alphaOfColor(int color) {
		return (color >> 24 & 0xff) / 255f;
	}
}