package com.flask.colorpicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ColorPickerDialogBuilder {
	private AlertDialog.Builder builder;
	private ColorPickerView colorPickerView;
	private LinearLayout pickerContainer;

	private ColorPickerDialogBuilder(Context context) {
		builder = new AlertDialog.Builder(context);
		pickerContainer = new LinearLayout(context);
		pickerContainer.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		colorPickerView = new ColorPickerView(context);
		colorPickerView.setLayoutParams(layoutParams);
		LightnessBar lightnessBar = new LightnessBar(context);
		lightnessBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64));

		pickerContainer.addView(colorPickerView);
		pickerContainer.addView(lightnessBar);
		colorPickerView.setLightnessBar(lightnessBar);
		builder.setView(pickerContainer);
	}

	public static ColorPickerDialogBuilder newPicker(Context context) {
		return new ColorPickerDialogBuilder(context);
	}

	public ColorPickerDialogBuilder setTitle(String title) {
		builder.setTitle(title);
		return this;
	}

	public ColorPickerDialogBuilder initialColor(int initialColor) {
		colorPickerView.setInitialColor(initialColor);
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
				onClickListener.onClick(dialog, colorPickerView.getSelectedColor());
			}
		});
		return this;
	}

	public ColorPickerDialogBuilder setNegativeButton(String text, DialogInterface.OnClickListener onClickListener) {
		builder.setNegativeButton(text, onClickListener);
		return this;
	}

	public AlertDialog build() {
		return builder.create();
	}
}