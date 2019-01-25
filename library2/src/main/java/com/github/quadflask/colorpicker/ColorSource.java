package com.github.quadflask.colorpicker;

import java.util.HashSet;
import java.util.Set;

public class ColorSource {

    private Color color;
    private Set<OnColorChangedListener> onColorChangedListenerList = new HashSet<>();

    public ColorSource(int argb) {
        this.color = new Color(argb);
    }

    public void updateAlpha(float alpha) {
        updateColor(color.withAlpha(alpha).getArgb());
    }

    public void updateLightness(float lightness) {
        updateColor(color.withLightness(lightness).getArgb());
    }

    public void updateColor(int newArgb) {
        final int argb = getARGB();
        if (argb != newArgb) {
            this.color = this.color.withColor(newArgb);
            for (OnColorChangedListener onColorChangedListener : onColorChangedListenerList) {
                onColorChangedListener.onColorChanged(getColor());
            }
        }
    }

    public void addListener(OnColorChangedListener listener) {
        onColorChangedListenerList.add(listener);
        listener.onColorChanged(getColor());
    }

    public void removeListener(OnColorChangedListener listener) {
        onColorChangedListenerList.remove(listener);
    }

    public void removeAllListeners() {
        onColorChangedListenerList.clear();
    }

    public int getARGB() {
        return color.getArgb();
    }

    public Color getColor() {
        return color;
    }
}
