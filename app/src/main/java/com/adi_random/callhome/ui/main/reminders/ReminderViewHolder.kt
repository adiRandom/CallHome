package com.adi_random.callhome.ui.main.reminders

import android.graphics.Bitmap
import android.graphics.drawable.TransitionDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.adi_random.callhome.R
import com.adi_random.callhome.databinding.ReminderItemBinding
import com.adi_random.callhome.model.Reminder
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


/**
 * Created by Adrian Pascu on 30-Aug-20
 */
class ReminderViewHolder(val binding: ReminderItemBinding) : RecyclerView.ViewHolder(binding.root) {


    fun bind(reminder: Reminder?, delete: () -> Unit) {
        binding.model = ReminderViewModel(reminder, delete)
        if (reminder?.hasError == true) {
//            Animate the background

            binding.reminderItemRoot.setBackgroundResource(R.drawable.error_light_transition)
            GlobalScope.launch {
                val animationDuration = 300
                (binding.reminderItemRoot.background as TransitionDrawable).apply {
                    delay(500)
                    startTransition(animationDuration)
                    delay(animationDuration.toLong())
                    reverseTransition(animationDuration)
                }
                delay(300)
                //Reset the background

                binding.reminderItemRoot.setBackgroundResource(R.drawable.reminder_ripple_background)
            }
        }
        binding.executePendingBindings()

    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
        object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int {
                return adapterPosition
            }

            override fun getSelectionKey(): Long? {
                return binding.model?.reminder?.reminderId
            }

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
        fun setImageSrc(view: ImageView, value: Bitmap?) {
            if (value != null)
                view.setImageBitmap(value)
            else {
                view.setImageResource(R.drawable.ic_baseline_person_32)
            }
        }
    }
}

data class ReminderViewModel(val reminder: Reminder?, val _delete: () -> Unit) {
    fun delete(view: View) {
        _delete()
    }
}