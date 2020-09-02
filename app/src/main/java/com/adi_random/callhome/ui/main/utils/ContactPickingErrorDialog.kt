package com.adi_random.callhome.ui.main.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.adi_random.callhome.R


class ContactPickingErrorDialog : DialogFragment() {

    private var callback: () -> Unit = {}

    fun setCallback(value: () -> Unit): ContactPickingErrorDialog {
        callback = value
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage("There was an error while picking the contact. Try again.")
            .setPositiveButton(
                "OK"
            ) { p0, _ ->
                callback()
                p0?.dismiss()
            }
            .create()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert_dialog, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ContactPickingErrorDialog()
    }
}