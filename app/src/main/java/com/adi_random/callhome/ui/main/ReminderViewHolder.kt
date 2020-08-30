package com.adi_random.callhome.ui.main

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adi_random.callhome.databinding.ReminderItemBinding
import com.adi_random.callhome.model.Reminder
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import java.util.*


/**
 * Created by Adrian Pascu on 30-Aug-20
 */
class ReminderViewHolder(val binding: ReminderItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(reminder: Reminder?, delete: () -> Unit) {
        binding.model = ReminderViewModel(reminder, delete)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("text")
        fun setReminderType(view: TextView, type: ReminderType) {
            val string = type.name.toLowerCase(Locale.getDefault())
                .capitalize(Locale.getDefault()) + " reminder"
            view.text = string
        }

        @JvmStatic
        @BindingAdapter("src")
        fun setImageSrc(view: ImageView, value: Bitmap) {
            view.setImageBitmap(value)
        }
    }
}

data class ReminderViewModel(val reminder: Reminder?, val _delete: () -> Unit) {
    fun delete(view: View) {
        _delete()
    }
}