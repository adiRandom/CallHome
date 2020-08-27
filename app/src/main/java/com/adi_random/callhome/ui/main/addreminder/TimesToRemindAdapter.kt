package com.adi_random.callhome.ui.main.addreminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adi_random.callhome.databinding.TimeToRemindItemBinding


/**
 * Created by Adrian Pascu on 24-Aug-20
 */
class TimesToRemindAdapter(
    var data: List<Int>,
    var type: ReminderType,
    private var onDelete: (pos: Int) -> Unit
) :
    RecyclerView.Adapter<TimesToRemindViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimesToRemindViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TimeToRemindItemBinding.inflate(inflater, parent, false)
        return TimesToRemindViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimesToRemindViewHolder, position: Int) {
        holder.bind(data[position], type, onDelete)
    }

    override fun getItemCount(): Int = data.size

}