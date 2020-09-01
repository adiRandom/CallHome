package com.adi_random.callhome.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adi_random.callhome.database.ReminderRepository
import com.adi_random.callhome.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragmentViewModel(app: Application) : AndroidViewModel(app) {

    private val context = app.applicationContext
    private val repository = ReminderRepository.getInstance(context)

    //    The position of the reminder to be deleted or -1 if no reminder gets deleted
    fun notifyDeletingReminder(pos: Int) {
        deletingReminder.value = pos
    }


    val reminders: LiveData<List<Reminder>> by lazy {
        repository.getRemindersAsLiveData()
    }

    fun deleteReminder(pos: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReminder(reminders.value?.get(pos)!!)
        }
    }

    /**
     * -1 indicates no reminder is getting deleted
     * A value of at least 0 means the reminder at that position in the list is getting deleted
     */
    private val deletingReminder: MutableLiveData<Int> by lazy {
        MutableLiveData(-1)
    }

    fun getDeletingReminder(): LiveData<Int> = deletingReminder

}