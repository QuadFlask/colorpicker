package com.flask.colorpicker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class ColorPickerPreference extends Preference {
	protected boolean alphaSlider;
	protected boolean lightSlider;
	protected int initialColor = 0;
	protected int selectedColor = 0;
	protected ColorPickerView.WHEEL_TYPE wheelType;
	protected int density;

	protected View selectedColorIndicator;
	private CircleColorDrawable circleColorDrawable;

	public ColorPickerPreference(Context context) {
		super(context);
	}

	public ColorPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWith(context, attrs);
	}

	public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initWith(context, attrs);
	}

	private void initWith(Context context, AttributeSet attrs) {
		final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference);
		alphaSlider = typedArray.getBoolean(R.styleable.ColorPickerPreference_alphaSlider, false);
		lightSlider = typedArray.getBoolean(R.styleable.ColorPickerPreference_lightnessSlider, false);
		density = typedArray.getInt(R.styleable.ColorPickerPreference_density, 10);
		initialColor = typedArray.getInt(R.styleable.ColorPickerPreference_initialColor, 0xffffffff);
		wheelType = ColorPickerView.WHEEL_TYPE.indexOf(typedArray.getInt(R.styleable.ColorPickerPreference_wheelType, 0));
		selectedColor = initialColor;

		setWidgetLayoutResource(R.layout.view_color_picker_preference);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		selectedColorIndicator = view.findViewById(R.id.v_color_indicator);
		circleColorDrawable = new CircleColorDrawable(initialColor);
		selectedColorIndicator.setBackgroundDrawable(circleColorDrawable);
		updateColorIndicator();
	}

	protected void updateColorIndicator() {
		circleColorDrawable.setColor(selectedColor);
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		if (restoreValue) {
			initialColor = getPersistedInt(initialColor);
			selectedColor = initialColor;
		} else {
			initialColor = (Integer) defaultValue;
			selectedColor = initialColor;
			persistInt(initialColor);
		}
	}

	@Override
	protected void onClick() {
		ColorPickerDialogBuilder builder = ColorPickerDialogBuilder
				.with(getContext())
				.setTitle("Choose color")
				.initialColor(selectedColor)
				.wheelType(wheelType)
				.density(density)
				.setPositiveButton("ok", new ColorPickerClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int selectedColorFromPicker, Integer[] allColors) {
						selectedColor = selectedColorFromPicker;
						persistInt(selectedColor);
						updateColorIndicator();
						notifyChanged();
					}
				})
				.setNegativeButton("cancel", null);

		if (!alphaSlider && !lightSlider) builder.noSliders();
		else if (alphaSlider && lightSlider) {
		} else if (alphaSlider) builder.alphaSliderOnly();
		else if (lightSlider) builder.lightnessSliderOnly();

		builder
				.build()
				.show();
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable parcelable = super.onSaveInstanceState();
		if (isPersistent()) return parcelable;

		final SavedState savedState = new SavedState(parcelable);
		savedState.setSelectedColor(this.selectedColor);
		return savedState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (!state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
		} else {
			SavedState savedState = (SavedState) state;
			super.onRestoreInstanceState(savedState.getSuperState());
			this.selectedColor = savedState.getSelectedColor();
			updateColorIndicator();
			notifyChanged();

			super.onRestoreInstanceState(state);
		}
	}

	static class SavedState extends BaseSavedState {
		private int selectedColor;

		@SuppressWarnings("unused")
		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(selectedColor);
		}

		public SavedState(Parcel source) {
			super(source);
			selectedColor = source.readInt();
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public void setSelectedColor(int selectedColor) {
			this.selectedColor = selectedColor;
		}

		public int getSelectedColor() {
			return selectedColor;
		}
	}
}