package com.flask.colorpicker;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ColorPickerDialogBuilder extends AlertDialog.Builder {
	private ColorPickerView colorPickerView;
	private LinearLayout pickerContainer;
	private LinearLayout currentColorIndicator;

	public ColorPickerDialogBuilder(Context context) {
		super(context);
		pickerContainer = new LinearLayout(context);
		pickerContainer.setOrientation(LinearLayout.VERTICAL);

		currentColorIndicator = new LinearLayout(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 48); // dp
		currentColorIndicator.setLayoutParams(lp);

		colorPickerView = new ColorPickerView(context);
		colorPickerView.setOnColorSelectedListener(new OnColorSelectedListener() {
			@Override
			public void onColorSelected(int selectedColor) {
				currentColorIndicator.setBackgroundColor(selectedColor);
			}
		});

		pickerContainer.addView(currentColorIndicator);
		pickerContainer.addView(colorPickerView);

		setView(pickerContainer);
	}

	public ColorPickerDialogBuilder setInitialColor(int color) {
		colorPickerView.setInitialColor(color);
		return this;
	}

}