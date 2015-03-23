package com.flask.colorpicker.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.flask.colorpicker.ColorPickerDialogBuilder;


public class SampleActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ColorPickerDialogBuilder colorPickerDialogBuilder = new ColorPickerDialogBuilder();
				AlertDialog.Builder builder = colorPickerDialogBuilder.build(SampleActivity.this);
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		});
	}
}
