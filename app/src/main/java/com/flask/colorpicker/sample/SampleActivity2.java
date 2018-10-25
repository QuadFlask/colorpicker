package com.flask.colorpicker.sample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

public class SampleActivity2 extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample2);

		ColorPickerView colorPickerView = findViewById(R.id.color_picker_view);
		colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
			@Override public void onColorChanged(int selectedColor) {
				// Handle on color change
				Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(selectedColor));
			}
		});
		colorPickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
			@Override
			public void onColorSelected(int selectedColor) {
				Toast.makeText(
						SampleActivity2.this,
						"selectedColor: " + Integer.toHexString(selectedColor).toUpperCase(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}