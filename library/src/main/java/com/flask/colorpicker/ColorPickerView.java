package com.flask.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flask.colorpicker.builder.PaintBuilder;
import com.flask.colorpicker.renderer.ColorWheelRenderOption;
import com.flask.colorpicker.renderer.ColorWheelRenderer;
import com.flask.colorpicker.slider.AlphaSlider;
import com.flask.colorpicker.slider.LightnessSlider;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

public class ColorPickerView extends View {
    private static final float STROKE_RATIO = 2f;

    private Bitmap colorWheel;
    private Canvas colorWheelCanvas;
    private int density = 10;

    private float lightness = 1;
    private float alpha = 1;
    private int backgroundColor = 0x00000000;

    private Integer initialColors[] = new Integer[]{null, null, null, null, null};
    private int colorSelection = 0;
    private Integer initialColor;
    private Paint colorWheelFill = PaintBuilder.newPaint().color(0).build();
    private Paint selectorStroke1 = PaintBuilder.newPaint().color(0xffffffff).build();
    private Paint selectorStroke2 = PaintBuilder.newPaint().color(0xff000000).build();
    private Paint alphaPatternPaint = PaintBuilder.newPaint().build();
    private ColorCircle currentColorCircle;

    private ArrayList<OnColorSelectedListener> onColorSelectedListeners = new ArrayList<OnColorSelectedListener>();
    private ArrayList<OnLightnessSelectedListener> onLightnessSelectedListeners = new ArrayList<OnLightnessSelectedListener>();
    private ArrayList<OnAlphaSelectedListener> onAlphaSelectedListeners = new ArrayList<OnAlphaSelectedListener>();
    private LightnessSlider lightnessSlider;
    private AlphaSlider alphaSlider;
    private MaterialEditText colorEdit;
    private TextWatcher colorTextChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                if (s == null)
                    return;
                int color = Color.parseColor(s.toString());
                setColor(color);
            } catch (Exception e) {
            }
        }
    };
    private LinearLayout colorPreview;

    private ColorWheelRenderer renderer;

    public ColorPickerView(Context context) {
        super(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        updateColorWheel();
    }

    private void updateColorWheel() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (height < width)
            width = height;
        if (width <= 0)
            return;
        if (colorWheel == null) {
            colorWheel = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            colorWheelCanvas = new Canvas(colorWheel);
            alphaPatternPaint.setShader(PaintBuilder.createAlphaPatternShader(8));
        }
        drawColorWheel();
    }

    private void drawColorWheel() {
        colorWheelCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

        float half = colorWheelCanvas.getWidth() / 2f;
        float strokeWidth = STROKE_RATIO * (1f + ColorWheelRenderer.GAP_PERCENTAGE);
        float maxRadius = half - strokeWidth - half / density;
        float cSize = maxRadius / (density - 1) / 2;

        ColorWheelRenderOption colorWheelRenderOption = renderer.getRenderOption();
        colorWheelRenderOption.density = this.density;
        colorWheelRenderOption.maxRadius = maxRadius;
        colorWheelRenderOption.cSize = cSize;
        colorWheelRenderOption.strokeWidth = strokeWidth;
        colorWheelRenderOption.alpha = alpha;
        colorWheelRenderOption.lightness = lightness;
        colorWheelRenderOption.targetCanvas = colorWheelCanvas;

        renderer.initWith(colorWheelRenderOption);

        renderer.draw();

        if (initialColor != null) {
            currentColorCircle = findNearestByColor(initialColor);
            float[] hsv = new float[3];
            Color.colorToHSV(initialColor, hsv);
            currentColorCircle.set(currentColorCircle.getX(), currentColorCircle.getY(), hsv);
            initialColor = null;
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
            height = widthMeasureSpec;
        else if (heightMode == MeasureSpec.AT_MOST)
            height = MeasureSpec.getSize(heightMeasureSpec);
        else if (widthMode == MeasureSpec.EXACTLY)
            height = MeasureSpec.getSize(heightMeasureSpec);
        int squareDimen = width;
        if (height < width)
            squareDimen = height;
        setMeasuredDimension(squareDimen, squareDimen);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                int selectedColor = getSelectedColor();
                currentColorCircle = findNearestByPosition(event.getX(), event.getY());
                setColorToSliders(selectedColor);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                int selectedColor = getSelectedColor();
                if (onColorSelectedListeners != null) {
                    for (OnColorSelectedListener listener : onColorSelectedListeners) {
                        try {
                            listener.onColorSelected(selectedColor);
                        } catch (Exception e) {
                            //Squash individual listener exceptions
                        }
                    }
                }
                setColorToSliders(selectedColor);
                setColorText(selectedColor, true);
                setColorPreviewColor(selectedColor);
                invalidate();
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(backgroundColor);
        if (colorWheel != null)
            canvas.drawBitmap(colorWheel, 0, 0, null);
        if (currentColorCircle != null) {
            float maxRadius = canvas.getWidth() / 2f - STROKE_RATIO * (1f + ColorWheelRenderer.GAP_PERCENTAGE);
            float size = maxRadius / density / 2;
            colorWheelFill.setColor(Color.HSVToColor(currentColorCircle.getHsvWithLightness(this.lightness)));
            colorWheelFill.setAlpha((int) (alpha * 0xff));
            canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size * STROKE_RATIO, selectorStroke1);
            canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size * (1 + (STROKE_RATIO - 1) / 2), selectorStroke2);

            canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size, alphaPatternPaint);
            canvas.drawCircle(currentColorCircle.getX(), currentColorCircle.getY(), size, colorWheelFill);
        }
    }

    private ColorCircle findNearestByPosition(float x, float y) {
        ColorCircle near = null;
        double minDist = Double.MAX_VALUE;

        for (ColorCircle colorCircle : renderer.getColorCircleList()) {
            double dist = colorCircle.sqDist(x, y);
            if (minDist > dist) {
                minDist = dist;
                near = colorCircle;
            }
        }

        return near;
    }

    private ColorCircle findNearestByColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        ColorCircle near = null;
        double minDiff = Double.MAX_VALUE;
        double x = hsv[1] * Math.cos(hsv[0] * Math.PI / 180);
        double y = hsv[1] * Math.sin(hsv[0] * Math.PI / 180);

        for (ColorCircle colorCircle : renderer.getColorCircleList()) {
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

    public int getSelectedColor() {
        int color = 0;
        if (currentColorCircle != null)
            color = Color.HSVToColor(currentColorCircle.getHsvWithLightness(this.lightness));
        return Utils.adjustAlpha(this.alpha, color);
    }

    public Integer[] getAllColors() {
        return initialColors;
    }

    public void setInitialColors(Integer[] colors, int selectedColor) {

        this.initialColors = colors;
        this.colorSelection = selectedColor;
        setInitialColor(this.initialColors[this.colorSelection]);
    }


    public void setInitialColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        this.alpha = Utils.getAlphaPercent(color);
        this.lightness = hsv[2];
        this.initialColors[this.colorSelection] = color;
        this.initialColor = color;
        setColorPreviewColor(color);
        setColorToSliders(color);
        if (this.colorEdit != null)
            setColorText(color, true);
        if (renderer.getColorCircleList() != null)
            currentColorCircle = findNearestByColor(color);
    }

    public void setLightness(float lightness, boolean notify) {
        this.lightness = lightness;
        this.initialColor = Color.HSVToColor(Utils.alphaValueAsInt(this.alpha), currentColorCircle.getHsvWithLightness(lightness));
        if (this.colorEdit != null)
            this.colorEdit.setText("#" + Integer.toHexString(this.initialColor).toUpperCase());
        if (this.alphaSlider != null && this.initialColor != null)
            this.alphaSlider.setColor(this.initialColor);
        updateColorWheel();
        invalidate();
        if (notify) {
            notifyLightnessListeners();
        }
    }

    private void notifyLightnessListeners() {
        if (onLightnessSelectedListeners.size() > 0) {
            int color = getSelectedColor();
            for (OnLightnessSelectedListener onLightnessSelectedListener : onLightnessSelectedListeners) {
                onLightnessSelectedListener.onLightnessSelected(color);
            }
        }
    }

    public void setColor(int color) {
        setInitialColor(color);
        updateColorWheel();
        invalidate();
    }

    public void setAlphaValue(float alpha, boolean notify) {
        this.alpha = alpha;
        this.initialColor = Color.HSVToColor(Utils.alphaValueAsInt(this.alpha), currentColorCircle.getHsvWithLightness(this.lightness));
        if (this.colorEdit != null)
            this.colorEdit.setText("#" + Integer.toHexString(this.initialColor).toUpperCase());
        if (this.lightnessSlider != null && this.initialColor != null)
            this.lightnessSlider.setColor(this.initialColor);
        updateColorWheel();
        invalidate();
        if (notify) {
            notifyAlphaListeners();
        }
    }

    private void notifyAlphaListeners() {
        if (onAlphaSelectedListeners.size() > 0) {
            int color = getSelectedColor();
            for (OnAlphaSelectedListener onAlphaSelectedListener : onAlphaSelectedListeners) {
                onAlphaSelectedListener.onAlphaSelected(color);
            }
        }
    }

    public void addOnColorSelectedListener(OnColorSelectedListener listener) {
        this.onColorSelectedListeners.add(listener);
    }

    public void addOnLightnessSelectedListener(OnLightnessSelectedListener listener) {
        this.onLightnessSelectedListeners.add(listener);
    }

    public void addOnAlphaSelectedListener(OnAlphaSelectedListener listener) {
        this.onAlphaSelectedListeners.add(listener);
    }

    public void setLightnessSlider(LightnessSlider lightnessSlider) {
        this.lightnessSlider = lightnessSlider;
        if (lightnessSlider != null)
            this.lightnessSlider.setColorPicker(this);
    }

    public void setAlphaSlider(AlphaSlider alphaSlider) {
        this.alphaSlider = alphaSlider;
        if (alphaSlider != null)
            this.alphaSlider.setColorPicker(this);
    }

    public void setColorEdit(MaterialEditText colorEdit) {
        this.colorEdit = colorEdit;
        if (this.colorEdit != null)
            this.colorEdit.setVisibility(View.VISIBLE);
    }

    public void setDensity(int density) {
        this.density = Math.max(2, density);
        invalidate();
    }

    public void setRenderer(ColorWheelRenderer renderer) {
        this.renderer = renderer;
        invalidate();
    }

    public void setColorPreview(LinearLayout colorPreview, Integer selectedColor) {
        if (colorPreview == null)
            return;
        this.colorPreview = colorPreview;
        if (selectedColor == null)
            selectedColor = 0;
        int children = colorPreview.getChildCount();
        if (children == 0 || colorPreview.getVisibility() != View.VISIBLE)
            return;

        for (int i = 0; i < children; i++) {
            View childView = colorPreview.getChildAt(i);
            if (!(childView instanceof LinearLayout))
                continue;
            LinearLayout childLayout = (LinearLayout) childView;
            if (i == selectedColor) {
                childLayout.setBackgroundColor(Color.WHITE);
            }
            ImageView childImage = (ImageView) childLayout.findViewById(R.id.image_preview);
            childImage.setClickable(true);
            childImage.setTag(i);
            childImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == null)
                        return;
                    Object tag = v.getTag();
                    if (tag == null || !(tag instanceof Integer))
                        return;
                    setSelectedColor((int) tag);
                }
            });
        }
    }

    public void setSelectedColor(int previewNumber) {
        if (initialColors == null || initialColors.length < previewNumber)
            return;
        this.colorSelection = previewNumber;
        setHighlightedColor(previewNumber);
        Integer color = initialColors[previewNumber];
        if (color == null)
            return;
        setColor(color);
    }

    private void setHighlightedColor(int previewNumber) {
        int children = colorPreview.getChildCount();
        if (children == 0 || colorPreview.getVisibility() != View.VISIBLE)
            return;

        for (int i = 0; i < children; i++) {
            View childView = colorPreview.getChildAt(i);
            if (!(childView instanceof LinearLayout))
                continue;
            LinearLayout childLayout = (LinearLayout) childView;
            if (i == previewNumber) {
                childLayout.setBackgroundColor(Color.WHITE);
            } else {
                childLayout.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private void setColorPreviewColor(int newColor) {
        if (colorPreview == null || initialColors == null || colorSelection > initialColors.length || initialColors[colorSelection] == null)
            return;

        int children = colorPreview.getChildCount();
        if (children == 0 || colorPreview.getVisibility() != View.VISIBLE)
            return;

        View childView = colorPreview.getChildAt(colorSelection);
        if (!(childView instanceof LinearLayout))
            return;
        LinearLayout childLayout = (LinearLayout) childView;
        ImageView childImage = (ImageView) childLayout.findViewById(R.id.image_preview);
        childImage.setImageDrawable(new ColorDrawable(newColor));
    }

    private void setColorText(int argb, boolean internal) {
        if (colorEdit == null)
            return;
        if (internal)
            colorEdit.removeTextChangedListener(colorTextChange);
        colorEdit.setText("#" + Integer.toHexString(argb));
        if (internal)
            colorEdit.addTextChangedListener(colorTextChange);
    }

    private void setColorToSliders(int selectedColor) {
        if (lightnessSlider != null)
            lightnessSlider.setColor(selectedColor);
        if (alphaSlider != null)
            alphaSlider.setColor(selectedColor);
    }

    public enum WHEEL_TYPE {
        FLOWER, CIRCLE
    }
}