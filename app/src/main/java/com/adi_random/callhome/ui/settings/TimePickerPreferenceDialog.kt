package com.adi_random.callhome.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import com.adi_random.callhome.R
import java.util.*


/**
 * Created by Adrian Pascu on 03-Sep-20
 */
class TimePickerPreferenceDialog : PreferenceDialogFragmentCompat() {


    private val calendar = Calendar.getInstance()
    private var timePicker: TimePicker? = null

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        super.onBindDialogView(view)
        view?.findViewById<TimePicker>(R.id.time_picker).apply {
            if (this != null) {
                timePicker = this
                setIs24HourView(true)
                if (preference is TimePickerPreference)
                    calendar.time = (preference as TimePickerPreference).getTime()

                //Bind to the time picker
                setOnTimeChangedListener { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                }

            }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult)
            if (preference is TimePickerPreference)
                (preference as TimePickerPreference).setTime(calendar.time)

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