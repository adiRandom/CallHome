package com.adi_random.callhome.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.adi_random.callhome.databinding.MainFragmentBinding
import com.adi_random.callhome.ui.main.addreminder.AddReminderErrorDialog
import com.adi_random.callhome.ui.main.addreminder.AddReminderFragment
import com.adi_random.callhome.ui.main.addreminder.AddReminderViewModel
import com.adi_random.callhome.ui.main.reminders.ErrorReminderItemAnimator
import com.adi_random.callhome.ui.main.reminders.ItemDetailsLookupImpl
import com.adi_random.callhome.ui.main.reminders.ReminderAdapter
import com.adi_random.callhome.ui.main.utils.ReminderTimesDialog
import com.adi_random.callhome.ui.main.utils.SimpleYesNoDialog

class MainFragment : Fragment() {

    companion object {
        fun newInstance(errorReminderId: Long?): MainFragment {
            val args = Bundle().apply {
                if (errorReminderId != null)
                    putLong(ERROR_REMINDER_ID_ARG, errorReminderId)
            }
            return MainFragment().apply {
                arguments = args
            }
        }

        const val DELETE_REMINDER_DIALOG_TAG = "delete_reminder_dialog"

        //        The selection id for the recyclerview selection tracker
        const val SELECTION_ID = "selection"
        const val REMIND_TIMES_DIALOG_TAG = "remind_times_dialog"
        const val ERROR_REMINDER_ID_ARG = "error_reminder"
    }

    private val viewModel: MainFragmentViewModel by viewModels()
    private val createReminderViewModel: AddReminderViewModel by activityViewModels()
    private lateinit var binding: MainFragmentBinding
    private lateinit var selectionTracker: SelectionTracker<Long>
    private var errorReminderId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            errorReminderId = it.getLong(ERROR_REMINDER_ID_ARG)
        }
    }

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

        val reminderAdapter by lazy {
            ReminderAdapter(viewModel.reminders.value ?: emptyList()) {
                viewModel.notifyDeletingReminder(it)
            }.apply {
                setHasStableIds(true)
            }
        }

        val layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return true
            }
        }


        binding.reminders.apply {
            adapter = reminderAdapter
            this.layoutManager = layoutManager
            itemAnimator = ErrorReminderItemAnimator()
        }


        //Create the selection tracker for the reminders list
        selectionTracker = SelectionTracker.Builder<Long>(
            SELECTION_ID,
            binding.reminders,
            StableIdKeyProvider(binding.reminders),
            ItemDetailsLookupImpl(binding.reminders),
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectSingleAnything())
            .build()

        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if (!selectionTracker.selection.isEmpty) {
                    val reminderId = selectionTracker.selection.iterator().next()
                    val reminder = viewModel.reminders.value?.find {
                        it.reminderId == reminderId
                    }
                    ReminderTimesDialog.newInstance(reminder).show(
                        childFragmentManager,
                        REMIND_TIMES_DIALOG_TAG
                    )
                }
            }
        })

//        Add the tracker to the adapter
        reminderAdapter.tracker = selectionTracker


        //Observing the reminder list
        viewModel.reminders.observe(viewLifecycleOwner) {

            reminderAdapter.apply {
                //If there is a reminder with error, toggle it's error graphics
                if (errorReminderId != null) {

                    val mappedList = it.map { reminder ->
                        if (reminder.reminderId == errorReminderId)
                            reminder.apply {
                                hasError = true
                            }
                        else
                            reminder
                    }
                    reminders = mappedList
                    notifyDataSetChanged()
                } else {
                    reminders = it
                    notifyDataSetChanged()
                }

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