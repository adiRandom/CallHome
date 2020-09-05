package com.adi_random.callhome.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.adi_random.callhome.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        setSupportActionBar(findViewById(R.id.toolbar2))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onDisplayPreferenceDialog(preference: Preference?) {
            if (parentFragmentManager.findFragmentByTag(TimePickerPreferenceDialog.DIALOG_TAG) != null) {
                return
            }

            if (preference is TimePickerPreference) {
                val dialog = TimePickerPreferenceDialog.newInstance(preference.getKey())
                dialog.setTargetFragment(this, 0)
                dialog.show(parentFragmentManager, TimePickerPreferenceDialog.DIALOG_TAG)
            } else {
                super.onDisplayPreferenceDialog(preference)
            }
        }
    }
}