package com.adi_random.callhome.ui.main.addreminder

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adi_random.callhome.databinding.TimeToRemindItemBinding


/**
 * Created by Adrian Pascu on 24-Aug-20
 */


class TimesToRemindViewHolder(private val binding: TimeToRemindItemBinding, type: ReminderType) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(value: String,type:ReminderType) {
        binding.value = value
        binding.type = type
        binding.executePendingBindings()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("android:text", "app:type")
        fun setItemText(view: TextView, text: String, type: ReminderType) {
            val value = when (type) {
                ReminderType.DAILY -> "At $text every day"
                ReminderType.WEEKLY -> {
                    val day: String = when (text) {
                        "1" -> "Monday"
                        "2" -> "Tuesday"
                        "3" -> "Wednesday"
                        "4" -> "Thursday"
                        "5" -> "Friday"
                        "6" -> "Saturday"
                        "7" -> "Sunday"
                        else -> ""
                    }
                    "Every $day"
                }
                ReminderType.MONTHLY -> {
                    val day = when (text) {
                        "1" -> "1st"
                        "2" -> "2nd"
                        "3" -> "3rd"
                        "21" -> "21st"
                        "22" -> "22nd"
                        "23" -> "23rd"
                        "31" -> "31st"
                        else -> "${text}th"
                    }
                    "The $day of every month"
                }
            }
            view.text = value
        }
    }


}