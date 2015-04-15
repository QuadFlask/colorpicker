package com.flask.colorpicker.builder;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class PaintBuilder {
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

		public PaintHolder xPerMode(PorterDuff.Mode mode) {
			this.paint.setXfermode(new PorterDuffXfermode(mode));
			return this;
		}

		public Paint build() {
			return this.paint;
		}
	}
}
