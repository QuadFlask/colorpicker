package com.flask.colorpicker;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ColorPickerDialogBuilder {
	private ColorPickerView colorPickerView;
	private LinearLayout pickerContainer;
	private LinearLayout currentColorIndicator;

	public AlertDialog.Builder build(Context context) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		pickerContainer = new LinearLayout(context);
		pickerContainer.setOrientation(LinearLayout.VERTICAL);

		currentColorIndicator = new LinearLayout(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80); // dp
		currentColorIndicator.setLayoutParams(lp);

		colorPickerView = new ColorPickerView(context);
		colorPickerView.setOnColorSelectedListener(new OnColorSelectedListener() {
			@Override
			public void onColorSelected(int selectedColor) {
				currentColorIndicator.setBackgroundColor(selectedColor);
			}
		});
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		colorPickerView.setLayoutParams(layoutParams);

		pickerContainer.addView(currentColorIndicator);
		pickerContainer.addView(colorPickerView);

		dialog.setView(pickerContainer);
		colorPickerView.setInitialColor(0xffff0000);

		return dialog;
	}

	public ColorPickerDialogBuilder setInitialColor(int color) {
		colorPickerView.setInitialColor(color);
		return this;
	}
}