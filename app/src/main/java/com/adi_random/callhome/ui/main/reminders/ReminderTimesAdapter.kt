package com.adi_random.callhome.ui.main.reminders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adi_random.callhome.R
import com.adi_random.callhome.util.RemindTime


/**
 * Created by Adrian Pascu on 01-Sep-20
 */
class ReminderTimesAdapter(private val dataset: List<RemindTime>?) :
    RecyclerView.Adapter<ReminderTimesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderTimesViewHolder =
        ReminderTimesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.reminder_popup_reminder_times, parent, false
            )
        )

    override fun onBindViewHolder(holder: ReminderTimesViewHolder, position: Int) {
        holder.bind(dataset?.get(position)?.toString())
    }

    override fun getItemCount(): Int = dataset?.size ?: 0
}