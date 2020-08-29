package com.adi_random.callhome.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.adi_random.callhome.R


/**
 * Created by Adrian Pascu on 29-Aug-20
 */
class CallLogPermissionDialog : DialogFragment() {

    private var callback: () -> Unit = {

    }

    fun setCallback(value: () -> Unit): CallLogPermissionDialog {
        this.callback = value
        return this
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
            .setTitle("Permission required")
            .setMessage("For this app to function properly you need to grant access to your call history")
            .setPositiveButton("OK") { _, _ ->
                callback()
                dismiss()
            }.create()


    companion object {
        fun newInstance() = CallLogPermissionDialog()
    }
}