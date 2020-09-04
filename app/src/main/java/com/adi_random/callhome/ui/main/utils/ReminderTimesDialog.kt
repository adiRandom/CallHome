package com.adi_random.callhome.ui.main.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adi_random.callhome.R
import com.adi_random.callhome.databinding.ReminderPopupBinding
import com.adi_random.callhome.model.Reminder
import com.adi_random.callhome.ui.main.reminders.ReminderTimesAdapter


/**
 * Created by Adrian Pascu on 01-Sep-20
 */
class ReminderTimesDialog(private val reminder: Reminder?) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alert_dialog, container, false)
    }

    private var callback: () -> Unit = {

    }

    fun setCallback(_callback: () -> Unit): ReminderTimesDialog {
        callback = _callback
        return this
    }

    override fun onDismiss(dialog: DialogInterface) {
        callback()
        super.onDismiss(dialog)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = ReminderPopupBinding.inflate(layoutInflater, null, false)
        binding.reminder = reminder
        binding.reminderTimes.apply {
            adapter = ReminderTimesAdapter(reminder?.timesToRemind)
            layoutManager = LinearLayoutManager(context)
        }
        return AlertDialog.Builder(context)
            .setView(binding.root)
            .setPositiveButton("OK") { _, _ ->
                callback()
                dismiss()
            }
            .create()

    }

    companion object {
        fun newInstance(reminder: Reminder?): ReminderTimesDialog = ReminderTimesDialog(reminder)
    }
}