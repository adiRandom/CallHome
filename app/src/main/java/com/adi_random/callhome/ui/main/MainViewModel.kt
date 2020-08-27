package com.adi_random.callhome.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.adi_random.callhome.database.ReminderRepository
import com.adi_random.callhome.model.Reminder

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val context = app.applicationContext
    private val repository = ReminderRepository.getInstance(context)

    val reminders: LiveData<List<Reminder>> by lazy {
        repository.getReminders().switchMap { list ->
            val mappedList = list.map { Reminder.fromReminderAndRemindTime(it) }
            MutableLiveData(mappedList)
        }
    }

}