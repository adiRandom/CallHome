package com.adi_random.callhome.model

import android.content.Context
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import com.adi_random.callhome.util.RemindTime


/**
 * Created by Adrian Pascu on 25-Aug-20
 */
class ReminderBuilder(private val context: Context) {
    private var contact: Contact = EMPTY_CONTACT
    private var type = ReminderType.WEEKLY
    private var timesToRemind = emptyList<Int>()

    fun withContact(contact: Contact): ReminderBuilder {
        this.contact = contact
        return this
    }

    fun withReminderType(type: ReminderType): ReminderBuilder {
        this.type = type
        return this
    }

    fun withTimesToRemind(list: List<Int>): ReminderBuilder {
        this.timesToRemind = list
        return this
    }

    suspend fun build(): Reminder {
        val remindTimes = timesToRemind.map { RemindTime(it, type) }
        val contentRetriever = ContentRetriever(context)
        val lastCallDate = contentRetriever.getLastCallDate(contact)
        return Reminder(contact, remindTimes, lastCallDate)
    }

}