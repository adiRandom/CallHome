package com.adi_random.callhome.model

import android.content.Context
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adi_random.callhome.database.ReminderRepository
import com.adi_random.callhome.database.models.ReminderAndRemindTime
import com.adi_random.callhome.util.RemindTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.TestOnly
import java.util.*

/**
 * Created by Adrian Pascu on 19-Aug-20
 */

@Entity
open class Reminder(
    @Embedded
    var contact: Contact,
//The call date from the last reminder
    var lastCallDate: Date,
    @PrimaryKey
    var reminderId: Long,
    val creationDate: Date = Date()
) {

    @Ignore
    var timesToRemind: List<RemindTime> = emptyList()

    /**
     *  If there is an error with this reminder (retrieving the contact, the last call date, etc.) count it
     *  If it reaches a threshold of 5, prompt the user to remove the reminder
     */
    var errorCount: Int = 0

    constructor(
        contact: Contact,
        timesToRemind: List<RemindTime>,
        lastCallDate: Date,
        reminderId: Long,
        errorCount: Int = 0,
        creationDate: Date = Date()
    ) : this(contact, lastCallDate, reminderId, creationDate) {
        this.timesToRemind = timesToRemind
        this.errorCount = errorCount

    }

    fun madeCall(date: Date) {
//        Update last call made
        lastCallDate = date
    }

    open suspend fun countError(context: Context) = withContext(Dispatchers.IO) {
        val repository = ReminderRepository.getInstance(context)
        errorCount++
        repository.countError(reminderId)
    }

    @TestOnly
    suspend fun countError(
        context: Context,
        repository: ReminderRepository,
        dispatcher: CoroutineDispatcher
    ) = withContext(dispatcher) {
        errorCount++
        repository.countError(reminderId)
    }


    companion object {
        fun fromReminderAndRemindTime(value: ReminderAndRemindTime): Reminder = Reminder(
            value.reminder.contact,
            value.timesToRemind,
            value.reminder.lastCallDate,
            value.reminder.reminderId,
            value.reminder.errorCount,
            value.reminder.creationDate
        )
    }

}