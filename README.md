Color Picker
-------------

![icon](https://github.com/QuadFlask/colorpicker/blob/master/app/src/main/res/drawable-xxxhdpi/ic_launcher.png)

simple android color picker with color wheel and lightness bar.


## Screenshot

![screenshot.png](https://github.com/QuadFlask/colorpicker/blob/master/screenshot/screenshot.png)

```java

ColorPickerDialogBuilder
	.newPicker(context)
	.setTitle("Choose color")
	.initialColor(0xffffffff)
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
