Color Picker
-------------

![icon](https://github.com/QuadFlask/colorpicker/blob/master/app/src/main/res/drawable-xxxhdpi/ic_launcher.png)

simple android color picker with color wheel and lightness bar.

[![App on PlayStore](http://www.android.com/images/brand/android_app_on_play_logo_small.png)](https://play.google.com/store/apps/details?id=com.flask.colorpicker.sample)

market link: [https://play.google.com/store/apps/details?id=com.flask.colorpicker.sample](https://play.google.com/store/apps/details?id=com.flask.colorpicker.sample)

## Demo video

[Youtube](https://youtu.be/MwWi9X7eqNI)


## Screenshot

### WHEEL_TYPE_FLOWER
![screenshot2.png](https://github.com/QuadFlask/colorpicker/blob/master/screenshot/screenshot2.png)

### WHEEL_TYPE.CIRCLE
![screenshot.png](https://github.com/QuadFlask/colorpicker/blob/master/screenshot/screenshot.png)


## How to add dependency?

This library is not released in Maven Central, but instead you can use [JitPack](https://jitpack.io)

add remote maven url

```groovy
repositories {
	maven {
		url "https://jitpack.io"
	}
}
````

then add a library dependency

```groovy
dependencies {
	compile 'com.github.QuadFlask:colorpicker:0.0.4aar'
}
```


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


## License

```
Copyright 2014-2015 QuadFlask

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
