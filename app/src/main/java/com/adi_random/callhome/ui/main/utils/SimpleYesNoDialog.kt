package com.adi_random.callhome.ui.main.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.adi_random.callhome.R


/**
 * Created by Adrian Pascu on 31-Aug-20
 */

const val TITLE_ARG = "title_arg"
const val MESSAGE_ARG = "message_arg"

class SimpleYesNoDialog : DialogFragment() {
    private lateinit var title: String
    private lateinit var message: String

    private var callback: () -> Unit = {

    }

    fun setCallback(value: () -> Unit): SimpleYesNoDialog {
        this.callback = value
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = requireArguments()
        title = args.getString(TITLE_ARG, "")
        message = args.getString(MESSAGE_ARG, "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alert_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                callback()
                dismiss()
            }
            .setNegativeButton("No") { _, _ ->
                dismiss()
            }
            .create()


    companion object {
        fun newInstance(title: String, message: String, callback: () -> Unit) =
            SimpleYesNoDialog().apply {
                arguments = bundleOf(TITLE_ARG to title, MESSAGE_ARG to message)
                setCallback(callback)
            }
    }
}