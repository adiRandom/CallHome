package com.adi_random.callhome.ui.main.addreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adi_random.callhome.R
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

    private lateinit var binding: FragmentAddReminderListDialogBinding
    private val viewModel: AddReminderViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Void>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        binding = FragmentAddReminderListDialogBinding.inflate(inflater,container,false)
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_add_reminder_list_dialog,
            container,
            false
        )
        binding.viewModel = viewModel

        //Bind contact picker icon to callback

        binding.contactPickerLayout.setEndIconOnClickListener {
            activityResultLauncher.launch(null)
        }

        //Bind the cancel button

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        //Subscribe to contact livedata on viewmodel

        viewModel.getContact().observe(viewLifecycleOwner){
            binding.viewModel = viewModel
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.PickContact()) {
            viewModel.setContact(it)
        }
    }

    companion object {

        fun newInstance(): AddReminder =
            AddReminder()
    }
}