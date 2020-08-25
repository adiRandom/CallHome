package com.adi_random.callhome.ui.main.addreminder

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adi_random.callhome.R
import com.adi_random.callhome.databinding.FragmentAddReminderListDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.jetbrains.annotations.TestOnly


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
    private val viewModel: AddReminderViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Void>

    @TestOnly
    fun _getViewModel() = viewModel

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

        //Bind the save button
        binding.saveButton.setOnClickListener {
            viewModel.createReminder()
            dismiss()
        }

        //Bind the add time button
        binding.addTimeButton.setOnClickListener {
            TimePopupFragment.newInstance(viewModel.getReminderType().value!!).show(
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

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.PickContact()) {
            if (it != null)
                viewModel.setContact(it)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
//        Clear the viewmodel
        viewModel.clear()
        viewModelStore.clear()

        super.onDismiss(dialog)
//        Unsubscribe from observer
        viewModel.getContact().removeObservers(viewLifecycleOwner)

    }

    companion object {

        const val TIME_PICKER_TAG = "time_picker_tag"

        fun newInstance(): AddReminderFragment =
            AddReminderFragment()
    }

    //Callback for picking time to remind
    fun onTimePicked(hour: Int, minute: Int) {
        viewModel.addTimeToRemind(hour, minute)
    }

    //Callback for picking time to remind
    fun onDayPicked(day: Int) {
        viewModel.addTimeToRemind(day)
    }

}