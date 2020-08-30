package com.adi_random.callhome.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adi_random.callhome.databinding.ReminderItemBinding
import com.adi_random.callhome.model.Reminder


/**
 * Created by Adrian Pascu on 30-Aug-20
 */
class ReminderAdapter(
    var reminders: List<Reminder>,
    private val delete: (pos: Int) -> Unit
) : RecyclerView.Adapter<ReminderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ReminderItemBinding.inflate(inflater, parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders.get(position)) {
            delete(position)
        }
    }

    override fun getItemCount(): Int = reminders.size

    override fun getItemId(position: Int): Long {
        return reminders[position].reminderId

    }

}