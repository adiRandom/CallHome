package com.adi_random.callhome.ui.main.addreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adi_random.callhome.databinding.FragmentAddReminderListDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    AddReminder.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class AddReminder : BottomSheetDialogFragment() {

    private lateinit var binding:FragmentAddReminderListDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddReminderListDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    companion object {

        fun newInstance(): AddReminder =
            AddReminder()
    }
}