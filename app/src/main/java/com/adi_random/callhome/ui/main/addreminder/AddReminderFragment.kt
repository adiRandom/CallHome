package com.adi_random.callhome.ui.main.addreminder

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adi_random.callhome.R
import com.adi_random.callhome.databinding.FragmentAddReminderListDialogBinding
import com.adi_random.callhome.ui.main.utils.ContactPickingErrorDialog
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
class AddReminderFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddReminderListDialogBinding
    private val viewModel: AddReminderViewModel by activityViewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Void>

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it)
                activityResultLauncher.launch(null)
        }


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
            pickContact()
        }

        binding.contactPickerLayout.setErrorIconOnClickListener {
            pickContact()
        }

        //Bind the cancel button

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        //Bind the save button
        binding.saveButton.setOnClickListener {
            if (viewModel.createReminder())
                dismiss()
            else {
                //Set contact picker error text
                binding.contactPickerLayout.error = "Error"
            }
        }

        //Bind the add time button
        binding.addTimeButton.setOnClickListener {
            TimePopupFragment.newInstance(viewModel.getReminderType().value!!)
                .setCallback(object : ITimePopupCallback {
                    override fun onTimePicked(hour: Int, minute: Int) {
                        viewModel.addTimeToRemind(hour, minute)
                    }

                    override fun onDayPicked(day: Int) {
                        viewModel.addTimeToRemind(day)
                    }

                })
                .show(
                    parentFragmentManager,
                    TIME_PICKER_TAG
                )
        }

        //Subscribe to contact livedata on viewmodel

        viewModel.getContact().observe(viewLifecycleOwner) {
            binding.viewModel = viewModel
        }

        //Bind to recyclerview
        binding.timesToRemind.apply {
            adapter = viewModel.timesToRemindAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        //Listen to radio checked changed
        binding.frequencyPicker.setOnCheckedChangeListener { _, id ->
            viewModel.onRadioCheckedChanged(id)
        }

        //Set the lifecycle owner
        binding.lifecycleOwner = viewLifecycleOwner

        //Subscribe to retrieval errors
        viewModel.getContactRetrievalError().observe(viewLifecycleOwner) {
            if (it)
                ContactPickingErrorDialog.newInstance()
                    .setCallback {
                        viewModel.dismissContactRetrievalError()
                    }
                    .show(
                        childFragmentManager,
                        CONTACT_PICKER_ERROR_DIALOG_TAG
                    )
        }





        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.PickContact()) {
            if (it != null)
                viewModel.setContact(it)
        }
    }

    fun pickContact() {
        //            Check permissions
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
        //Permission was already granted. Show the picker
            activityResultLauncher.launch(null)
        else {
            //Permission wasn't yet granted
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.clear()
        super.onDismiss(dialog)
    }

    companion object {

        const val TIME_PICKER_TAG = "time_picker_tag"
        const val CONTACT_PICKER_ERROR_DIALOG_TAG = "contact_retrieval_error"
        const val ADD_REMINDER_ERROR_DIALOG_TAG = "add_reminder_error"

        fun newInstance(): AddReminderFragment =
            AddReminderFragment()
    }

}