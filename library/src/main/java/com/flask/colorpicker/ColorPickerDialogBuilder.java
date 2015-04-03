package com.flask.colorpicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ColorPickerDialogBuilder {
	private AlertDialog.Builder builder;
	private LinearLayout pickerContainer;
	private ColorPickerView colorPickerView;
	private LightnessSlider lightnessSlider;
	private AlphaSlider alphaSlider;

	private boolean isLightnessSliderEnabled = true;
	private boolean isAlphaSliderEnabled = true;
	private int defaultMargin = 0;

	private ColorPickerDialogBuilder(Context context) {
		builder = new AlertDialog.Builder(context);
		pickerContainer = new LinearLayout(context);
		pickerContainer.setOrientation(LinearLayout.VERTICAL);
		defaultMargin = getDimensionAsPx(context, R.dimen.default_bar_margin);

		LinearLayout.LayoutParams layoutParamsForColorPickerView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		colorPickerView = new ColorPickerView(context);
		colorPickerView.setLayoutParams(layoutParamsForColorPickerView);

		pickerContainer.addView(colorPickerView);
		builder.setView(pickerContainer);
	}

	public static int getDimensionAsPx(Context context, int rid) {
		return (int) (context.getResources().getDimension(rid) + .5f);
	}

	public static ColorPickerDialogBuilder with(Context context) {
		return new ColorPickerDialogBuilder(context);
	}

	public ColorPickerDialogBuilder setTitle(String title) {
		builder.setTitle(title);
		return this;
	}

	public ColorPickerDialogBuilder initialColor(int initialColor) {
		colorPickerView.setInitialColor(initialColor);
		lightnessSlider.setColor(initialColor);
		alphaSlider.setColor(initialColor);
		return this;
	}

	public ColorPickerDialogBuilder wheelType(ColorPickerView.WHEEL_TYPE wheelType) {
		colorPickerView.setWheelType(wheelType);
		return this;
	}

	public ColorPickerDialogBuilder density(int density) {
		colorPickerView.setDensity(density);
		return this;
	}

	public ColorPickerDialogBuilder setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
		colorPickerView.setOnColorSelectedListener(onColorSelectedListener);
		return this;
	}

	public ColorPickerDialogBuilder setPositiveButton(String text, final DialogInterface.OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int selectedColor = colorPickerView.getSelectedColor();
				onClickListener.onClick(dialog, selectedColor);
			}
		});
		return this;
	}

	public ColorPickerDialogBuilder setNegativeButton(String text, DialogInterface.OnClickListener onClickListener) {
		builder.setNegativeButton(text, onClickListener);
		return this;
	}

	public ColorPickerDialogBuilder noSliders() {
		isLightnessSliderEnabled = false;
		isAlphaSliderEnabled = false;
		return this;
	}

	public ColorPickerDialogBuilder alphaSliderOnly() {
		isLightnessSliderEnabled = false;
		isAlphaSliderEnabled = true;
		return this;
	}

	public ColorPickerDialogBuilder lightnessSliderOnly() {
		isLightnessSliderEnabled = true;
		return this;
	}

	public AlertDialog build() {
		Context context = builder.getContext();
		if (isLightnessSliderEnabled) {
			LinearLayout.LayoutParams layoutParamsForLightnessBar = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionAsPx(context, R.dimen.default_bar_height));
			layoutParamsForLightnessBar.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
			lightnessSlider = new LightnessSlider(context);
			lightnessSlider.setLayoutParams(layoutParamsForLightnessBar);
			pickerContainer.addView(lightnessSlider);
			colorPickerView.setLightnessSlider(lightnessSlider);
		}
		if (isAlphaSliderEnabled) {
			LinearLayout.LayoutParams layoutParamsForAlphaBar = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionAsPx(context, R.dimen.default_bar_height));
			layoutParamsForAlphaBar.setMargins(defaultMargin, 0, defaultMargin, defaultMargin);
			alphaSlider = new AlphaSlider(context);
			alphaSlider.setLayoutParams(layoutParamsForAlphaBar);
			pickerContainer.addView(alphaSlider);
			colorPickerView.setAlphaSlider(alphaSlider);
		}
		return builder.create();
	}
}