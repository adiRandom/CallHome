package com.adi_random.callhome.model

import android.content.Context
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import com.adi_random.callhome.util.RemindTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*


/**
 * Created by Adrian Pascu on 25-Aug-20
 */
class ReminderBuilder(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private var contact: Contact = EMPTY_CONTACT
    private var type = ReminderType.WEEKLY
    private var timesToRemind = emptyList<Int>()
    private var id: Long = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE

    fun withContact(contact: Contact?): ReminderBuilder {
        if (contact != null)
            this.contact = contact
        return this
    }

    fun withReminderType(type: ReminderType?): ReminderBuilder {

        if (type != null)
            this.type = type
        return this
    }

    fun withTimesToRemind(list: List<Int>?): ReminderBuilder {
        if (list != null)
            this.timesToRemind = list
        return this
    }

    fun withId(id: Long?): ReminderBuilder {
        if (id != null)
            this.id = id
        return this
    }

    suspend fun build(): Reminder? {
        val remindTimes = timesToRemind.map { RemindTime(it, type, reminderId = id) }

//        Build the cron instances
        remindTimes.forEach {
            it.buildCron(context)
        }

        val contentRetriever = ContentRetriever(context, dispatcher)
        return try {
            val lastCallDate = contentRetriever.getLastCallDate(contact)
            //    If there was no call made to this contact, create a default lastCallDate based on the time the reminder was created
            Reminder(contact, remindTimes, lastCallDate ?: Date(), id)
        } catch (e: Error) {
            null
        }
    }

}