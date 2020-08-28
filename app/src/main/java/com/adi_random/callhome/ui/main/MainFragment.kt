package com.adi_random.callhome.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.adi_random.callhome.R
import com.adi_random.callhome.ui.main.addreminder.AddReminderErrorDialog
import com.adi_random.callhome.ui.main.addreminder.AddReminderFragment
import com.adi_random.callhome.ui.main.addreminder.AddReminderViewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()
    private val createReminderViewModel: AddReminderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

        viewModel.reminders.observe(viewLifecycleOwner) {
//            TODO: Bind
        }

        return inflater.inflate(R.layout.main_fragment, container, false)
    }


}