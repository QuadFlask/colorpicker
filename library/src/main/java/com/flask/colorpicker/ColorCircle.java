package com.flask.colorpicker;

public class ColorCircle {
	private float x, y;
	private float[] hsv = new float[3];

	public ColorCircle(float x, float y, float[] hsv) {
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