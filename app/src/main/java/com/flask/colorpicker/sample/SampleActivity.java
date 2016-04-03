package com.flask.colorpicker.sample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;

public class SampleActivity extends ActionBarActivity {
	private View root;
	private int currentBackgroundColor = 0xffffffff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		root = findViewById(R.id.color_screen);
		changeBackgroundColor(currentBackgroundColor);

		findViewById(R.id.btn_dialog).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Context context = SampleActivity.this;

				ColorPickerDialogBuilder
						.with(context)
						.setTitle("Choose color")
						.initialColor(currentBackgroundColor)
						.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
						.density(12)
						.setOnColorSelectedListener(new OnColorSelectedListener() {
							@Override
							public void onColorSelected(int selectedColor) {
								toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
							}
						})
						.setPositiveButton("ok", new ColorPickerClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
								changeBackgroundColor(selectedColor);
								if (allColors != null) {
									StringBuilder sb = null;

									for (Integer color : allColors) {
										if (color == null)
											continue;
										if (sb == null)
											sb = new StringBuilder("Color List:");
										sb.append("\r\n#" + Integer.toHexString(color).toUpperCase());
									}

									if (sb != null)
										Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
								}
							}
						})
						.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.showColorEdit(true)
						.setColorEditTextColor(getResources().getColor(android.R.color.holo_blue_bright))
						.build()
						.show();
			}
		});
		findViewById(R.id.btn_prefs).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SampleActivity.this, PrefsActivity.class));
			}
		});
		findViewById(R.id.btn_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SampleActivity.this, SampleActivity2.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.btn_github).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = "https://github.com/QuadFlask/colorpicker";
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
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
