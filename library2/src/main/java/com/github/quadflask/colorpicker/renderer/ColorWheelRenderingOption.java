package com.github.quadflask.colorpicker.renderer;

public class ColorWheelRenderingOption {
    public final int width, density;
    public final float maxRadius;
    public final float cSize, strokeWidth;
    public final float gapPercentage, strokeRatio;

    public ColorWheelRenderingOption(int density, float gapPercentage, float strokeRatio) {
        this(
                0,
                density,
                0,
                0,
                0,
                gapPercentage,
                strokeRatio
        );
    }

    public ColorWheelRenderingOption(int width, int density, float maxRadius, float cSize, float strokeWidth, float gapPercentage, float strokeRatio) {
        this.width = width;
        this.density = density;
        this.maxRadius = maxRadius;
        this.cSize = cSize;
        this.strokeWidth = strokeWidth;
        this.gapPercentage = gapPercentage;
        this.strokeRatio = strokeRatio;
    }

    public ColorWheelRenderingOption withWidth(int width) {
        float half = width / 2f;

        float strokeWidth = strokeRatio * (1f + gapPercentage);
        float maxRadius = half - strokeWidth - half / density;
        float cSize = maxRadius / (density - 1) / 2;

        return new ColorWheelRenderingOption(
                width,
                density,
                maxRadius,
                cSize,
                strokeWidth,
                gapPercentage,
                strokeRatio
        );
    }
}
