package com.adi_random.callhome.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adi_random.callhome.database.models.ReminderAndRemindTime
import com.adi_random.callhome.util.RemindTime
import java.util.*

/**
 * Created by Adrian Pascu on 19-Aug-20
 */

@Entity
class Reminder(
    @Embedded
    var contact: Contact,

    var lastCallDate: Date,

    @PrimaryKey()
    var reminderId: String
) {

    @Ignore
    var timesToRemind: List<RemindTime> = emptyList()

    constructor(
        contact: Contact,
        timesToRemind: List<RemindTime>,
        lastCallDate: Date,
        reminderId: String
    ) : this(contact, lastCallDate, reminderId) {
        this.timesToRemind = timesToRemind

    }

    fun madeCall(date: Date) {
//        Update last call made
        lastCallDate = date
    }


    companion object {
        const val JOB_KEY = "reminders"
        fun fromReminderAndRemindTime(value: ReminderAndRemindTime): Reminder = Reminder(
            value.reminder.contact,
            value.timesToRemind,
            value.reminder.lastCallDate,
            value.reminder.reminderId
        )
    }

}