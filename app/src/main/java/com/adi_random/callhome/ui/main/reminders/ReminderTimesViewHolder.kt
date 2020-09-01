package com.adi_random.callhome.ui.main.reminders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adi_random.callhome.R


/**
 * Created by Adrian Pascu on 01-Sep-20
 */
class ReminderTimesViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(value: String?) {
        view.findViewById<TextView>(R.id.reminder_time).apply {
            if (value != null)
                text = value
        }
    }
}