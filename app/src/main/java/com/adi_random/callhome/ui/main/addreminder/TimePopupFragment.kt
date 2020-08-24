package com.adi_random.callhome.ui.main.addreminder

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.adi_random.callhome.R

//Argument key for dialog type
const val ARG_TYPE = "type"


class TimePopupFragment : DialogFragment() {
    private lateinit var type: ReminderType
    private lateinit var callback: ITimePipupCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = ReminderType.getReminderTypeFromInt(requireArguments().getInt(ARG_TYPE))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = (context as ITimePipupCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_popup, container, false)
    }

    private fun createDialog(dataArrayResId: Int): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Pick a day")
        val view = layoutInflater.inflate(R.layout.popup_day_content, null)
        view.findViewById<Spinner>(R.id.day_picker).apply {
            ArrayAdapter.createFromResource(
                requireContext(),
                dataArrayResId,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                this.adapter = adapter
            }
        }
        return builder.setView(view)
            .setPositiveButton("OK") { _, _ ->
                val day = view.findViewById<Spinner>(R.id.day_picker).selectedItemPosition + 1
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
            ReminderType.WEEKLY -> createDialog(R.array.week_days_array)
            ReminderType.MONTHLY -> createDialog(R.array.month_days_array)
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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(type: ReminderType) =
            TimePopupFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TYPE, type.value)
                }
            }
    }
}