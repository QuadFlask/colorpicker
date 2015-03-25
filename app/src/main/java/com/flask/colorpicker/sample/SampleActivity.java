package com.flask.colorpicker.sample;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerDialogBuilder;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;

public class SampleActivity extends ActionBarActivity {
	private RelativeLayout root;
	private int currentBackgroundColor = 0xffffffff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		root = (RelativeLayout) findViewById(R.id.rl_root);
		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Context context = SampleActivity.this;

				ColorPickerDialogBuilder
						.with(context)
						.setTitle("Choose color")
						.initialColor(currentBackgroundColor)
						.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
						.setOnColorSelectedListener(new OnColorSelectedListener() {
							@Override
							public void onColorSelected(int selectedColor) {
								toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
							}
						})
						.setPositiveButton("ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int selectedColor) {
								changeBackgroundColor(selectedColor);
							}
						})
						.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.build()
						.show();
			}
		});
	}

	private void changeBackgroundColor(int selectedColor) {
		currentBackgroundColor = selectedColor;
		root.setBackgroundColor(selectedColor);
	}

	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
