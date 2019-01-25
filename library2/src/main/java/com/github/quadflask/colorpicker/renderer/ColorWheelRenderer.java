package com.github.quadflask.colorpicker.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.quadflask.colorpicker.Color;
import com.github.quadflask.colorpicker.ColorCircle;
import com.github.quadflask.colorpicker.PaintBuilder;

import java.util.ArrayList;
import java.util.List;

public class ColorWheelRenderer {
    private final ColorWheelRenderingOption colorWheelRenderingOption;
    private List<ColorCircle> colorCircleList = new ArrayList<>();

    public ColorWheelRenderer(ColorWheelRenderingOption colorWheelRenderingOption) {
        this.colorWheelRenderingOption = colorWheelRenderingOption;
    }

    public void  render(Canvas canvas, Color color) {
        final ColorWheelRenderingOption option = this.colorWheelRenderingOption.withWidth(canvas.getWidth());

        Paint selectorFill = PaintBuilder.newPaint().build();
        float[] hsv = new float[3];
        float sizeJitter = 1.2f;

        final int setSize = colorCircleList.size();
        int currentCount = 0;
        float half = option.width/ 2f;
        int density = option.density;
        float strokeWidth = option.strokeWidth;
        float maxRadius = option.maxRadius;
        float cSize = option.cSize;

        int alpha = color.getAlphaAsInt();
        float lightness = color.getLightness();

        for (int i = 0; i < density; i++) {
            float p = (float) i / (density - 1); // 0~1
            float jitter = (i - density / 2f) / density; // -0.5 ~ 0.5
            float radius = maxRadius * p;
            float size = Math.max(1.5f + strokeWidth, cSize + (i == 0 ? 0 : cSize * sizeJitter * jitter));
            int total = Math.min(calcTotalCount(radius, size), density * 2);

            for (int j = 0; j < total; j++) {
                double angle = Math.PI * 2 * j / total + (Math.PI / total) * ((i + 1) % 2);
                float x = half + (float) (radius * Math.cos(angle));
                float y = half + (float) (radius * Math.sin(angle));
                hsv[0] = (float) (angle * 180 / Math.PI);
                hsv[1] = radius / maxRadius;
                hsv[2] = lightness;
                selectorFill.setColor(android.graphics.Color.HSVToColor(hsv));
                selectorFill.setAlpha(alpha);

                canvas.drawCircle(x, y, size - strokeWidth, selectorFill);

                if (currentCount >= setSize) {
                    colorCircleList.add(new ColorCircle(x, y, size - strokeWidth, hsv));
                } else colorCircleList.get(currentCount).set(x, y, size - strokeWidth, hsv);
                currentCount++;
            }
        }
    }

    protected int calcTotalCount(float radius, float size) {
        return Math.max(1, (int) ((1f - colorWheelRenderingOption.gapPercentage) * Math.PI / (Math.asin(size / radius)) + 0.5f));
    }

    public ColorCircle findNearestByColor(int color) {
        float[] hsv = new float[3];
        android.graphics.Color.colorToHSV(color, hsv);
        ColorCircle near = null;
        double minDiff = Double.MAX_VALUE;
        double x = hsv[1] * Math.cos(hsv[0] * Math.PI / 180);
        double y = hsv[1] * Math.sin(hsv[0] * Math.PI / 180);

        for (ColorCircle colorCircle : colorCircleList) {
            float[] hsv1 = colorCircle.getHsv();
            double x1 = hsv1[1] * Math.cos(hsv1[0] * Math.PI / 180);
            double y1 = hsv1[1] * Math.sin(hsv1[0] * Math.PI / 180);
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

    public ColorCircle findNearestByPosition(float x, float y) {
        ColorCircle near = null;
        double minDist = Double.MAX_VALUE;

        for (ColorCircle colorCircle : colorCircleList) {
            double dist = colorCircle.sqDist(x, y);
            if (minDist > dist) {
                minDist = dist;
                near = colorCircle;
            }
        }

        return near;
    }
}
