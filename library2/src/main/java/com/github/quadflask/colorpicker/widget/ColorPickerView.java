package com.github.quadflask.colorpicker.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.github.quadflask.colorpicker.ColorSource;
import com.github.quadflask.colorpicker.R;
import com.github.quadflask.colorpicker.renderer.ColorWheelRenderer;
import com.github.quadflask.colorpicker.renderer.ColorWheelRenderingOption;
import com.github.quadflask.colorpicker.widget.slider.AlphaSliderView;
import com.github.quadflask.colorpicker.widget.wheel.WheelView;

public class ColorPickerView extends FrameLayout {
    private static final float GAP_PERCENTAGE = 0.025f;
    private static final float STROKE_RATIO = 1.5f;

    private ColorPickerAttributes option;
    private ColorSource colorSource = new ColorSource(0xffffffff);

    private WheelView wheelView;
    private ColorCursorView colorCursorView;

    public ColorPickerView(Context context) {
        super(context);
        initWith(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWith(context, attrs);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWith(context, attrs);
    }

    @TargetApi(21)
    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initWith(context, attrs);
    }

    private void initWith(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference);
        this.option = resolveStyledAttributes(typedArray);
        typedArray.recycle();

        initRenderer(option);
    }

    private void initRenderer(ColorPickerAttributes option) {
        removeAllViews();
        colorSource.updateColor(option.initialColor);

        ColorWheelRenderer renderer = new ColorWheelRenderer(option.toColorWheelRenderingOption());
        wheelView = new WheelView(getContext()).initWith(renderer, colorSource);
        colorCursorView = new ColorCursorView(getContext()).initWith(renderer, colorSource);

        addView(wheelView);
        addView(colorCursorView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (option.alphaSliderViewId != 0)
            addSlider((AlphaSliderView) getRootView().findViewById(option.alphaSliderViewId));
    }

    private void addSlider(AlphaSliderView sliderView) {
        if (sliderView != null)
            sliderView.setColorSource(this.colorSource);
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

    private ColorPickerAttributes resolveStyledAttributes(final TypedArray typedArray) {
        int density = typedArray.getInt(R.styleable.ColorPickerPreference_density, 10);
        int initialColor = typedArray.getInt(R.styleable.ColorPickerPreference_initialColor, 0xffffffff);
        int pickerColorEditTextColor = typedArray.getInt(R.styleable.ColorPickerPreference_pickerColorEditTextColor, 0xffffffff);
        WHEEL_TYPE wheelType = WHEEL_TYPE.indexOf(typedArray.getInt(R.styleable.ColorPickerPreference_wheelType, 0));
        int alphaSliderViewId = typedArray.getResourceId(R.styleable.ColorPickerPreference_alphaSliderView, 0);
        int lightnessSliderViewId = typedArray.getResourceId(R.styleable.ColorPickerPreference_lightnessSliderView, 0);

        return new ColorPickerAttributes(
                density,
                initialColor,
                pickerColorEditTextColor,
                wheelType,
                alphaSliderViewId,
                lightnessSliderViewId
        );
    }

    class ColorPickerAttributes {
        int density;
        int initialColor;
        int pickerColorEditTextColor;
        WHEEL_TYPE wheelType;
        int alphaSliderViewId;
        int lightnessSliderViewId;

        ColorPickerAttributes(int density,
                              int initialColor,
                              int pickerColorEditTextColor,
                              WHEEL_TYPE wheelType,
                              int alphaSliderViewId,
                              int lightnessSliderViewId
        ) {
            this.density = density;
            this.initialColor = initialColor;
            this.pickerColorEditTextColor = pickerColorEditTextColor;
            this.wheelType = wheelType;
            this.alphaSliderViewId = alphaSliderViewId;
            this.lightnessSliderViewId = lightnessSliderViewId;
        }

        public ColorWheelRenderingOption toColorWheelRenderingOption() {
            return new ColorWheelRenderingOption(density, GAP_PERCENTAGE, STROKE_RATIO);
        }
    }

    public enum WHEEL_TYPE {
        FLOWER, CIRCLE;

        public static WHEEL_TYPE indexOf(int index) {
            switch (index) {
                case 0:
                    return FLOWER;
                case 1:
                    return CIRCLE;
            }
            return FLOWER;
        }
    }
}
