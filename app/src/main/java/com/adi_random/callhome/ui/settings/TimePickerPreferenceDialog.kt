package com.adi_random.callhome.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceDialogFragmentCompat


/**
 * Created by Adrian Pascu on 03-Sep-20
 */
class TimePickerPreferenceDialog : PreferenceDialogFragmentCompat() {
    override fun onDialogClosed(positiveResult: Boolean) {

    }

    companion object {
        const val DIALOG_TAG = "PREFERENCE_TIME_PICKER_DIALOG"

        fun newInstance(key: String) = TimePickerPreferenceDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_KEY, key)
            }
        }
    }
}