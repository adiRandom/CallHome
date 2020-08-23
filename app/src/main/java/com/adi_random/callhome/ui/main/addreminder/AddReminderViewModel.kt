package com.adi_random.callhome.ui.main.addreminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adi_random.callhome.model.Contact


/**
 * Created by Adrian Pascu on 23-Aug-20
 */
class AddReminderViewModel: ViewModel() {
    private val contact:MutableLiveData<Contact> by lazy {
        MutableLiveData<Contact>()
    }

   fun getContact():LiveData<Contact> = contact

    private val timesToRemind:MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }
    fun getTimesToRemind():LiveData<List<String>> = timesToRemind

    enum class ReminderType{
        DAILY,WEEKLY,MONTHLY
    }

    private val reminderType:MutableLiveData<ReminderType> by lazy {
        MutableLiveData<ReminderType>()
    }
    fun getReminderType():LiveData<ReminderType> = reminderType

}