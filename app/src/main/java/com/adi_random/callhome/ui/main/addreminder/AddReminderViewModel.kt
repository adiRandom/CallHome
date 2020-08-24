package com.adi_random.callhome.ui.main.addreminder

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private val timesToRemind: MutableList<Int> = emptyList<Int>().toMutableList()
    fun getTimesToRemind(): List<Int> = timesToRemind

    fun addTimeToRemind(hour: Int, min: Int) {
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
        if (!timesToRemind.contains(day)) {
            timesToRemind.add(day)
            timesToRemind.sort()
            val pos = timesToRemind.indexOf(day)
            timesToRemindAdapter.notifyItemInserted(pos)
        }
    }

    fun removeTimeToRemind(pos: Int) {
        timesToRemind.removeAt(pos)
        timesToRemindAdapter.notifyItemRemoved(pos)
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
        TimesToRemindAdapter(timesToRemind, reminderType.value ?: ReminderType.WEEKLY)
//
    fun clear() {
        reminderType.postValue(ReminderType.WEEKLY)
        _contact.value = EMPTY_CONTACT
        timesToRemind.clear()
        timesToRemindAdapter = TimesToRemindAdapter(emptyList(), ReminderType.WEEKLY)
    }
}