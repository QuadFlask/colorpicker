package com.flask.colorpicker.sample;

import android.os.Bundle;

import com.github.quadflask.colorpicker.widget.ColorPickerView;

import androidx.appcompat.app.AppCompatActivity;

public class SampleActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample4);

        ColorPickerView colorPickerView = findViewById(R.id.color_picker_view);
    }
}