package com.flask.colorpicker;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ColorPickerPreference extends Preference {
	public ColorPickerPreference(Context context) {
		super(context);
	}

	public ColorPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return li.inflate(R.layout.view_color_picker_preference, parent, false);
	}
}