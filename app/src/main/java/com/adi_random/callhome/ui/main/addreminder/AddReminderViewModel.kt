package com.adi_random.callhome.ui.main.addreminder

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adi_random.callhome.R
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.database.ReminderRepository
import com.adi_random.callhome.model.Contact
import com.adi_random.callhome.model.EMPTY_CONTACT
import com.adi_random.callhome.model.ReminderBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
    private val context = app.applicationContext
    private val contentRetriever = ContentRetriever(context)
    private val _contact: MutableLiveData<Contact> by lazy {
        MutableLiveData<Contact>(EMPTY_CONTACT)
    }

    fun getContact(): LiveData<Contact> = _contact

    private val timesToRemind: MutableList<Int> = emptyList<Int>().toMutableList()

    fun addTimeToRemind(hour: Int, _min: Int) {
//        Set times to remind error to false
        timesToRemindError.value = false
        val min = if (_min < 10) "0${_min}"; else _min
        val value = "${hour}${min}".toInt()
        if (!timesToRemind.contains(value)) {
            timesToRemind.add(value)
            timesToRemind.sort()
            val pos = timesToRemind.indexOf(value)
            timesToRemindAdapter.notifyItemInserted(pos)
        }
    }


    /**
     *       1-7 for day of the week
     *       1-31 for day of the month
     */
    fun addTimeToRemind(day: Int) {
        //        Set times to remind error to false
        timesToRemindError.value = false
        if (!timesToRemind.contains(day)) {
            timesToRemind.add(day)
            timesToRemind.sort()
            val pos = timesToRemind.indexOf(day)
            timesToRemindAdapter.notifyItemInserted(pos)
        }
    }

    private fun removeTimeToRemind(pos: Int) {
        timesToRemindAdapter.notifyItemRemoved(pos)
        timesToRemind.removeAt(pos)
    }


    private val reminderType: MutableLiveData<ReminderType> by lazy {
        MutableLiveData<ReminderType>(ReminderType.WEEKLY)
    }

    fun getReminderType(): LiveData<ReminderType> = reminderType

    /**
     * @return false if the retrieval failed
     */
    fun setContact(uri: Uri) {
//        Set contact error to false
        contactError.value = false
        viewModelScope.launch {
            val id = contentRetriever.getContactIdFromUri(uri)
            if (id != null) {
                val contact = contentRetriever.getContact(id)
                _contact.postValue(contact)
            } else {
                _contact.postValue(EMPTY_CONTACT)
                contactRetrievalError.postValue(true)
            }
        }
    }

    fun onRadioCheckedChanged(id: Int) {
        when (id) {
            R.id.daily ->
                reminderType.value = ReminderType.DAILY

            R.id.weekly ->
                reminderType.value = ReminderType.WEEKLY

            R.id.monthly ->
                reminderType.value = ReminderType.MONTHLY
        }

        //Empty the list
        timesToRemind.clear()
        //Update the adapter
        timesToRemindAdapter.apply {
            data = timesToRemind
            type = getReminderType().value!!
            notifyDataSetChanged()
        }
    }

    var timesToRemindAdapter =
        TimesToRemindAdapter(
            timesToRemind,
            reminderType.value ?: ReminderType.WEEKLY,
            this::removeTimeToRemind
        )


    private val contactError: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun getContactError(): LiveData<Boolean> = contactError

    private val timesToRemindError: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun getTimesToRemindError(): LiveData<Boolean> = timesToRemindError

    //    Returns true if the reminder was created and false if there were input errors
    fun createReminder(): Boolean {

//        Check for errors
        if (_contact.value == EMPTY_CONTACT)
            contactError.value = true

        if (timesToRemind.isEmpty())
            timesToRemindError.value = true

        if (timesToRemindError.value != false && contactError.value != false) {
            viewModelScope.launch {
                val reminder = ReminderBuilder(context)
                    .withReminderType(reminderType.value)
                    .withContact(_contact.value)
                    .withTimesToRemind(timesToRemind)
                    .build()
                if (reminder != null)
                    withContext(Dispatchers.IO)
                    { ReminderRepository.getInstance(context).insertReminder(reminder) }
                else {
                    reminderSavingError.postValue(true)
                }
            }
            return true
        }
        return false
    }

    private val contactRetrievalError: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    fun getContactRetrievalError(): LiveData<Boolean> = contactRetrievalError

    fun dismissContactRetrievalError() {
        contactRetrievalError.value = false
    }

    private val reminderSavingError: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    fun getReminderSavingError(): LiveData<Boolean> = reminderSavingError

    fun dismissReminderSavingError() {
        reminderSavingError.value = false
    }
}