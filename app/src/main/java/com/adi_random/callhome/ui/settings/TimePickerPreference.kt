package com.adi_random.callhome.ui.settings

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.adi_random.callhome.R
import java.util.*


/**
 * Created by Adrian Pascu on 03-Sep-20
 */
class TimePickerPreference(context: Context, attr: AttributeSet) : DialogPreference(context, attr) {

    companion object {
        fun getDefaultValue(): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 9)
            calendar.set(Calendar.MINUTE, 0)

            return calendar.time.time
        }
    }

    private val calendar = Calendar.getInstance()

    init {
        isPersistent = true
        dialogLayoutResource = R.layout.time_picker_preference
        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)


        calendar.time = Date(getDefaultValue())

        title = "Pick a time"
    }


    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a?.getString(index) ?: ""
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        val persistedValue = getPersistedLong(0)


        if (persistedValue == 0L) {
//            Not set yet
            persistLong(getDefaultValue())
            calendar.time = Date(getDefaultValue())
        } else {
            calendar.time = Date(persistedValue)
        }

    }

    override fun getSummary(): CharSequence {
        val _hour = calendar.get(Calendar.HOUR_OF_DAY)
        val _min = calendar.get(Calendar.MINUTE)

        val hour = if (_hour < 10) "0$_hour" else _hour.toString()
        val minute = if (_min < 10) "0$_min" else _min.toString()
        return "$hour:$minute"
    }

    fun setTime(value: Date) {
        calendar.time = value
        persistLong(value.time)
        notifyChanged()
    }

    fun getTime(): Date = calendar.time

}