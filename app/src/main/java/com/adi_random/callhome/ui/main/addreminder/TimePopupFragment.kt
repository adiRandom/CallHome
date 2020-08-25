package com.adi_random.callhome.ui.main.addreminder

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.adi_random.callhome.R
import com.adi_random.callhome.databinding.PopupDayContentBinding

//Argument key for dialog type
const val ARG_TYPE = "type"


class TimePopupFragment : DialogFragment() {
    private lateinit var type: ReminderType
    private lateinit var callback: ITimePipupCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = ReminderType.getReminderTypeFromInt(requireArguments().getInt(ARG_TYPE))
    }

    fun setCallback(callback: ITimePipupCallback):TimePopupFragment{
        this.callback = callback
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_popup, container, false)
    }

    private fun createDialog(type: ReminderType): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Pick a day")
//        Bindings for the dialog content
        val binding = PopupDayContentBinding.inflate(layoutInflater, null, false)
        binding.type = type
//
        return builder.setView(binding.root)
            .setPositiveButton("OK") { _, _ ->
                val day = binding.dayPicker.selectedItemPosition + 1
                callback.onDayPicked(day)
            }
            .setNegativeButton("CANCEL") { _, _ ->
                dismiss()
            }
            .create()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return when (type) {
            ReminderType.DAILY -> {
//                    Use TimeDialog
                TimePickerDialog(
                    requireContext(),
                    { _, p1, p2 -> callback.onTimePicked(p1, p2) }, 0, 0, true
                )
            }
            else -> createDialog(type)

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param type The type of dialog.
         * @return A new instance of fragment TimePopup.
         */
        @JvmStatic
        fun newInstance(type: ReminderType) =
            TimePopupFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TYPE, type.value)
                }
            }
    }
}