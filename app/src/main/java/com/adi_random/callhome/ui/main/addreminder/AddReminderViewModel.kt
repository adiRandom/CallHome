package com.adi_random.callhome.ui.main.addreminder

import android.app.Application
import android.net.Uri
import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.*
import com.adi_random.callhome.R
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.model.Contact
import com.adi_random.callhome.model.EMPTY_CONTACT
import kotlinx.coroutines.launch


/**
 * Created by Adrian Pascu on 23-Aug-20
 */


enum class ReminderType(val value: Int) {
    DAILY(0), WEEKLY(1), MONTHLY(2);

    companion object {
        @JvmStatic
        fun getReminderTypeFromInt(value: Int) = when (value) {
            0 -> DAILY
            1 -> WEEKLY
            2 -> MONTHLY
            else -> WEEKLY
        }
    }
}

class AddReminderViewModel(app: Application) : AndroidViewModel(app) {
    private val contentRetriever = ContentRetriever(app.applicationContext)
    private val _contact: MutableLiveData<Contact> by lazy {
        MutableLiveData<Contact>(EMPTY_CONTACT)
    }

    fun getContact(): LiveData<Contact> = _contact

    val timesToRemind: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>(emptyList<String>())
    }

    fun getTimesToRemind(): LiveData<List<String>> = timesToRemind

    fun addTimeToRemind(hour: Int, min: Int) {
        timesToRemind.switchMap {
            val newList = it + "${hour}:${min}"
            MutableLiveData<List<String>>(newList)
        }
    }

    /**
     *       1-7 for day of the week
     *       1-31 for day of the month
     */
    fun addTimeToRemind(day: Int) {
        timesToRemind.value.let {
            if(it != null)
           {
               val newList = it + day.toString()
               timesToRemind.value = newList
           }
        }
    }


    private val reminderType: MutableLiveData<ReminderType> by lazy {
        MutableLiveData<ReminderType>(ReminderType.WEEKLY)
    }

    fun getReminderType(): LiveData<ReminderType> = reminderType

    fun setContact(uri: Uri) {
        viewModelScope.launch {
            val id = contentRetriever.getContactIdFromUri(uri)
            val contact = contentRetriever.getContact(id)
            _contact.postValue(contact)
        }
    }

    fun onRadioClicked(view: View) {
        (view as RadioButton).apply {
            when (id) {
                R.id.daily -> if (isChecked) {
                    reminderType.postValue(ReminderType.DAILY)
//                    Empty the list of times
                    timesToRemind.value = (emptyList<String>().toMutableList())
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