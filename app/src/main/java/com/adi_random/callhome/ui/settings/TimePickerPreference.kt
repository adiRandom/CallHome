package com.adi_random.callhome.ui.settings

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.TimePicker
import androidx.preference.DialogPreference
import androidx.preference.PreferenceViewHolder
import com.adi_random.callhome.R
import java.util.*


/**
 * Created by Adrian Pascu on 03-Sep-20
 */
class TimePickerPreference(context: Context, attr: AttributeSet) : DialogPreference(context, attr) {

    init {
        dialogLayoutResource = R.layout.time_picker_preference
        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)
    }


    private val calendar = Calendar.getInstance()
    private var timePicker: TimePicker? = null

    //    Get a reference to the TimePicker
    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        holder?.itemView?.findViewById<TimePicker>(R.id.time_picker).apply {
            if (this != null) {
                timePicker = this
                setIs24HourView(true)
                hour = this@TimePickerPreference.calendar.get(Calendar.HOUR_OF_DAY)
                minute = this@TimePickerPreference.calendar.get(Calendar.MINUTE)

//                Listen for input
                setOnTimeChangedListener { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                }
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a?.getString(index) ?: ""
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        val now = Date()
        val persistedValue = getPersistedLong(now.time)
        calendar.time = now

        if (shouldPersist())
            persistLong(now.time)
    }

    override fun getSummary(): CharSequence {
        val _hour = calendar.get(Calendar.HOUR_OF_DAY)
        val _min = calendar.get(Calendar.MINUTE)

        val hour = if (_hour < 10) "0$_hour" else _hour.toString()
        val minute = if (_min < 10) "0$_min" else _min.toString()
        return "$hour:$minute"
    }


}