package com.github.quadflask.colorpicker;

public class Color {
    private int argb;

    public Color(int argb) {
        this.argb = argb;
    }

    public Color withAlpha(float alpha) {
        return new Color(Utils.withAlpha(argb, alpha));
    }

    public Color withLightness(float lightness) {
        return new Color(Utils.withLightness(argb, lightness));
    }

    public Color withColor(int newArgb) {
        return new Color(newArgb);
    }

    public int getArgb() {
        return argb;
    }

    public float getAlpha() {
        return Utils.getAlpha(argb);
    }

    public float getLightness() {
        return Utils.getLightness(argb);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return argb == color.argb;
    }

    @Override
    public int hashCode() {
        return argb;
    }

    public int getAlphaAsInt() {
        return Math.round(getAlpha() * 255);
    }
}
