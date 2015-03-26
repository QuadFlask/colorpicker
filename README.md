Color Picker
-------------

![icon](https://github.com/QuadFlask/colorpicker/blob/master/app/src/main/res/drawable-xxxhdpi/ic_launcher.png)

simple android color picker with color wheel and lightness bar.

[![App on PlayStore](http://www.android.com/images/brand/android_app_on_play_logo_small.png)](https://play.google.com/store/apps/details?id=com.flask.colorpicker.sample)

market link: [https://play.google.com/store/apps/details?id=com.flask.colorpicker.sample](https://play.google.com/store/apps/details?id=com.flask.colorpicker.sample)

## Screenshot

### WHEEL_TYPE_FLOWER
![screenshot2.png](https://github.com/QuadFlask/colorpicker/blob/master/screenshot/screenshot2.png)

### WHEEL_TYPE.CIRCLE
![screenshot.png](https://github.com/QuadFlask/colorpicker/blob/master/screenshot/screenshot.png)


## Usage

```java

ColorPickerDialogBuilder
	.with(context)
	.setTitle("Choose color")
	.initialColor(0xffffffff)
	.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
	.density(12)
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

```

## To do

* gradle support
* performance improvement
* refactoring
