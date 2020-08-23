package com.adi_random.callhome.ui.main.addreminder

import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adi_random.callhome.R
import com.adi_random.callhome.model.Contact


/**
 * Created by Adrian Pascu on 23-Aug-20
 */
class AddReminderViewModel : ViewModel() {
    private val contact: MutableLiveData<Contact> by lazy {
        MutableLiveData<Contact>()
    }

    fun getContact(): LiveData<Contact> = contact

    private val timesToRemind: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }

    fun getTimesToRemind(): LiveData<List<String>> = timesToRemind

    enum class ReminderType {
        DAILY, WEEKLY, MONTHLY
    }

    private val reminderType: MutableLiveData<ReminderType> by lazy {
        MutableLiveData<ReminderType>()
    }

    fun getReminderType(): LiveData<ReminderType> = reminderType

    fun onRadioClicked(view: View) {
        (view as RadioButton).apply {
            when (id) {
                R.id.daily -> if (isChecked) {
                    reminderType.postValue(ReminderType.DAILY)
//                    Empty the list of times
                    timesToRemind.postValue(emptyList<String>().toMutableList())
                }
                R.id.weekly -> if (isChecked) {
                    reminderType.postValue(ReminderType.WEEKLY)
//                    Empty the list of times
                    timesToRemind.postValue(emptyList<String>().toMutableList())
                }
                R.id.monthly -> if (isChecked) {
                    reminderType.postValue(ReminderType.MONTHLY)
//                    Empty the list of times
                    timesToRemind.postValue(emptyList<String>().toMutableList())
                }
            }
        }
    }

}