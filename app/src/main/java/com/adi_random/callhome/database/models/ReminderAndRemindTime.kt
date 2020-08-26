package com.adi_random.callhome.database.models

import androidx.room.Embedded
import androidx.room.Relation
import com.adi_random.callhome.model.Reminder
import com.adi_random.callhome.util.RemindTime


/**
 * Created by Adrian Pascu on 26-Aug-20
 */
data class ReminderAndRemindTime(
    @Embedded val reminder: Reminder,
    @Relation(
        parentColumn = "reminderId",
        entityColumn = "reminderId"
    ) val timesToRemind: List<RemindTime>
)