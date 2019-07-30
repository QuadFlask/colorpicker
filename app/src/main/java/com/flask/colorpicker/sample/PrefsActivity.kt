package com.flask.colorpicker.sample

import android.os.Bundle

import androidx.preference.PreferenceFragmentCompat

class PrefsActivity : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
    }
}