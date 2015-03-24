package com.flask.colorpicker.sample;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerDialogBuilder;
import com.flask.colorpicker.OnColorSelectedListener;


public class SampleActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Context context = SampleActivity.this;
				int initialColor = 0xffff0000;

				ColorPickerDialogBuilder
						.newPicker(context)
						.setTitle("Choose color")
						.initialColor(initialColor)
						.setOnColorSelectedListener(new OnColorSelectedListener() {
							@Override
							public void onColorSelected(int selectedColor) {
								toast("onColorSelected: " + selectedColor);
							}
						})
						.setPositiveButton("ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								toast("ok");
							}
						})
						.setNegativeButton("no", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								toast("no");
							}
						})
						.build()
						.show();
			}
		});
	}

	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
