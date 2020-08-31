package com.adi_random.callhome.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adi_random.callhome.databinding.MainFragmentBinding
import com.adi_random.callhome.ui.main.addreminder.AddReminderErrorDialog
import com.adi_random.callhome.ui.main.addreminder.AddReminderFragment
import com.adi_random.callhome.ui.main.addreminder.AddReminderViewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        const val DELETE_REMINDER_DIALOG_TAG = "delete_reminder_dialog"
    }

    private val viewModel: MainFragmentViewModel by viewModels()
    private val createReminderViewModel: AddReminderViewModel by activityViewModels()
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MainFragmentBinding.inflate(inflater, container, false)

//        Listen to errors from the creation of reminders

        createReminderViewModel.getReminderSavingError().observe(viewLifecycleOwner) {
            if (it)
                AddReminderErrorDialog.newInstance()
                    .setCallback {
                        createReminderViewModel.dismissReminderSavingError()
                    }
                    .show(
                        childFragmentManager,
                        AddReminderFragment.ADD_REMINDER_ERROR_DIALOG_TAG
                    )
        }

        //Create recycler view
        val layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return true
            }
        }

        binding.reminders.apply {
            adapter = viewModel.reminderAdapter
            this.layoutManager = layoutManager
        }


        //Observing the reminder list
        viewModel.reminders.observe(viewLifecycleOwner) {
            viewModel.reminderAdapter.apply {
                reminders = it
                notifyDataSetChanged()
            }
        }

        //Display the deleting dialog when user tries to delete a reminder
        viewModel.getDeletingReminder().observe(viewLifecycleOwner) {
            if (it >= 0)
                SimpleYesNoDialog.newInstance(
                    "Delete reminder",
                    "Are you sure you want to delete this reminder?"
                ) {
                    viewModel.deleteReminder(it)
                }.show(childFragmentManager, DELETE_REMINDER_DIALOG_TAG)
        }

        return binding.root
    }


}