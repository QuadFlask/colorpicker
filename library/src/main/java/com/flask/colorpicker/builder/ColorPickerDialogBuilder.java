package com.flask.colorpicker.builder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.R;
import com.flask.colorpicker.renderer.ColorWheelRenderer;
import com.flask.colorpicker.slider.AlphaSlider;
import com.flask.colorpicker.slider.LightnessSlider;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ColorPickerDialogBuilder {
	private AlertDialog.Builder builder;
	private LinearLayout pickerContainer;
	private ColorPickerView colorPickerView;
	private LightnessSlider lightnessSlider;
	private AlphaSlider alphaSlider;
	private MaterialEditText colorEdit;
	private LinearLayout colorPreview;

	private boolean isLightnessSliderEnabled = true;
	private boolean isAlphaSliderEnabled = true;
	private boolean isColorEditEnabled = false;
	private boolean isPreviewEnabled = false;
	private int pickerCount = 1;
	private int defaultMargin = 0;
	private Integer[] initialColor = new Integer[]{null, null, null, null, null};

	private ColorPickerDialogBuilder(Context context) {
		builder = new AlertDialog.Builder(context);
		pickerContainer = new LinearLayout(context);
		pickerContainer.setOrientation(LinearLayout.VERTICAL);
		pickerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
		defaultMargin = getDimensionAsPx(context, R.dimen.default_slider_margin);

		LinearLayout.LayoutParams layoutParamsForColorPickerView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
		layoutParamsForColorPickerView.weight = 1;
		colorPickerView = new ColorPickerView(context);

		pickerContainer.addView(colorPickerView, layoutParamsForColorPickerView);

		builder.setView(pickerContainer);
	}

	public static ColorPickerDialogBuilder with(Context context) {
		return new ColorPickerDialogBuilder(context);
	}

	public ColorPickerDialogBuilder setTitle(String title) {
		builder.setTitle(title);
		return this;
	}

	public ColorPickerDialogBuilder initialColor(int initialColor) {
		this.initialColor[0] = initialColor;
		return this;
	}


	public ColorPickerDialogBuilder initialColors(int[] initialColor) {
		for (int i = 0; i < initialColor.length && i < this.initialColor.length; i++) {
			this.initialColor[i] = initialColor[i];
		}
		return this;
	}

	public ColorPickerDialogBuilder wheelType(ColorPickerView.WHEEL_TYPE wheelType) {
		ColorWheelRenderer renderer = ColorWheelRendererBuilder.getRenderer(wheelType);
		colorPickerView.setRenderer(renderer);
		return this;
	}

	public ColorPickerDialogBuilder density(int density) {
		colorPickerView.setDensity(density);
		return this;
	}

	public ColorPickerDialogBuilder setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
		colorPickerView.addOnColorSelectedListener(onColorSelectedListener);
		return this;
	}

	public ColorPickerDialogBuilder setPositiveButton(String text, final ColorPickerClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int selectedColor = colorPickerView.getSelectedColor();
				Integer[] allColors = colorPickerView.getAllColors();
				onClickListener.onClick(dialog, selectedColor, allColors);
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
		isAlphaSliderEnabled = false;
		return this;
	}

	public ColorPickerDialogBuilder showAlphaSlider(boolean showAlpha) {
		isAlphaSliderEnabled = showAlpha;
		return this;
	}

	public ColorPickerDialogBuilder showLightnessSlider(boolean showLightness) {
		isLightnessSliderEnabled = showLightness;
		return this;
	}

	public ColorPickerDialogBuilder showColorEdit(boolean showEdit) {
		isColorEditEnabled = showEdit;
		return this;
	}

	public ColorPickerDialogBuilder showColorPreview(boolean showPreview) {
		isPreviewEnabled = showPreview;
		if (!showPreview)
			pickerCount = 1;
		return this;
	}

	public ColorPickerDialogBuilder setPickerCount(int pickerCount) throws IndexOutOfBoundsException {
		if (pickerCount < 1 || pickerCount > 5)
			throw new IndexOutOfBoundsException("Picker Can Only Support 1-5 Colors");
		this.pickerCount = pickerCount;
		if (this.pickerCount > 1)
			this.isPreviewEnabled = true;
		return this;
	}

	public AlertDialog build() {
		Context context = builder.getContext();
		colorPickerView.setInitialColors(initialColor, getStartOffset(initialColor));

		if (isLightnessSliderEnabled) {
			LinearLayout.LayoutParams layoutParamsForLightnessBar = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionAsPx(context, R.dimen.default_slider_height));
			layoutParamsForLightnessBar.setMargins(defaultMargin, 0, defaultMargin, 0);
			lightnessSlider = new LightnessSlider(context);
			lightnessSlider.setLayoutParams(layoutParamsForLightnessBar);
			pickerContainer.addView(lightnessSlider);
			colorPickerView.setLightnessSlider(lightnessSlider);
			lightnessSlider.setColor(getStartColor(initialColor));
		}
		if (isAlphaSliderEnabled) {
			LinearLayout.LayoutParams layoutParamsForAlphaBar = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionAsPx(context, R.dimen.default_slider_height));
			layoutParamsForAlphaBar.setMargins(defaultMargin, 0, defaultMargin, 0);
			alphaSlider = new AlphaSlider(context);
			alphaSlider.setLayoutParams(layoutParamsForAlphaBar);
			pickerContainer.addView(alphaSlider);
			colorPickerView.setAlphaSlider(alphaSlider);
			alphaSlider.setColor(getStartColor(initialColor));
		}
		if (isColorEditEnabled) {
			LinearLayout.LayoutParams layoutParamsForColorEdit = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			int padSide = getDimensionAsPx(context, R.dimen.default_padding_side);
			layoutParamsForColorEdit.leftMargin = padSide;
			layoutParamsForColorEdit.rightMargin = padSide;
			colorEdit = (MaterialEditText) View.inflate(context, R.layout.picker_edit, null);
			colorEdit.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
			colorEdit.setMaxCharacters(9);
			colorEdit.setVisibility(View.GONE);
			pickerContainer.addView(colorEdit, layoutParamsForColorEdit);

			colorEdit.setText("#" + Integer.toHexString(getStartColor(initialColor)).toUpperCase());
			colorPickerView.setColorEdit(colorEdit);
		}
		if (isPreviewEnabled) {
			colorPreview = (LinearLayout) View.inflate(context, R.layout.color_preview, null);
			colorPreview.setVisibility(View.GONE);
			pickerContainer.addView(colorPreview);

			if (initialColor.length == 0) {
				ImageView colorImage = (ImageView) View.inflate(context, R.layout.color_selector, null);
				colorImage.setImageDrawable(new ColorDrawable(Color.WHITE));
			} else {
				for (int i = 0; i < initialColor.length && i < this.pickerCount; i++) {
					if (initialColor[i] == null)
						break;
					LinearLayout colorLayout = (LinearLayout) View.inflate(context, R.layout.color_selector, null);
					ImageView colorImage = (ImageView) colorLayout.findViewById(R.id.image_preview);
					colorImage.setImageDrawable(new ColorDrawable(initialColor[i]));
					colorPreview.addView(colorLayout);
				}
			}
			colorPreview.setVisibility(View.VISIBLE);
			colorPickerView.setColorPreview(colorPreview, getStartOffset(initialColor));
		}

		return builder.create();
	}

	private Integer getStartOffset(Integer[] colors) {
		Integer start = null;
		for (int i = 0; i < colors.length; i++) {
			if (colors[i] == null) {
				return start;
			}
			start = (i + 1) / 2;
		}
		return start;
	}

	private int getStartColor(Integer[] colors) {
		Integer startColor = getStartOffset(colors);
		return startColor == null ? Color.WHITE : colors[startColor];
	}

	private static int getDimensionAsPx(Context context, int rid) {
		return (int) (context.getResources().getDimension(rid) + .5f);
	}
}