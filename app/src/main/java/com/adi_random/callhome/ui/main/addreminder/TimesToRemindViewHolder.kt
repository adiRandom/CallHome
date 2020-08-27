package com.adi_random.callhome.ui.main.addreminder

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adi_random.callhome.databinding.TimeToRemindItemBinding


/**
 * Created by Adrian Pascu on 24-Aug-20
 */


class TimesToRemindViewHolder(private val binding: TimeToRemindItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    class ViewModel(
        var value: Int,
        var type: ReminderType,
        var _onDelete: () -> Unit
    ) {

        fun onDelete() {
            _onDelete()
        }

    }

    fun bind(value: Int, type: ReminderType, onDelete: (pos: Int) -> Unit) {
        binding.viewModel = ViewModel(value, type) {
            onDelete(adapterPosition)
        }

        binding.executePendingBindings()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("android:text", "app:type")
        fun setItemText(view: TextView, text: Int, type: ReminderType) {
            val value = when (type) {
                ReminderType.DAILY -> {
                    val hour = if (text / 100 < 10) "0${text / 100}"; else text / 100
                    val minute = if (text % 100 < 10) "0${text % 100}"; else text % 100
                    "At $hour:$minute every day"
                }
                ReminderType.WEEKLY -> {
                    val day: String = when (text) {
                        1 -> "Monday"
                        2 -> "Tuesday"
                        3 -> "Wednesday"
                        4 -> "Thursday"
                        5 -> "Friday"
                        6 -> "Saturday"
                        7 -> "Sunday"
                        else -> ""
                    }
                    "Every $day"
                }
                ReminderType.MONTHLY -> {
                    val day = when (text) {
                        1 -> "1st"
                        2 -> "2nd"
                        3 -> "3rd"
                        21 -> "21st"
                        22 -> "22nd"
                        23 -> "23rd"
                        31 -> "31st"
                        else -> "${text}th"
                    }
                    "The $day of every month"
                }
            }
            view.text = value
        }
    }


}