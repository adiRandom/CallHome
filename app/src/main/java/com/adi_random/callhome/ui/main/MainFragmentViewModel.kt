package com.adi_random.callhome.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.adi_random.callhome.database.ReminderRepository
import com.adi_random.callhome.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragmentViewModel(app: Application) : AndroidViewModel(app) {

    private val context = app.applicationContext
    private val repository = ReminderRepository.getInstance(context)
    val reminderAdapter by lazy {
        ReminderAdapter(reminders.value ?: emptyList(), this::deleteReminder).apply {
            setHasStableIds(true)
        }
    }

    val reminders: LiveData<List<Reminder>> by lazy {
        repository.getRemindersAsLiveData()
    }

    private fun deleteReminder(pos: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReminder(reminders.value?.get(pos)!!)
        }
    }

}