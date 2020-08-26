package com.adi_random.callhome.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.adi_random.callhome.database.models.ReminderAndRemindTime
import com.adi_random.callhome.model.Reminder


/**
 * Created by Adrian Pascu on 26-Aug-20
 */
@Dao
interface ReminderDao {

    @Query("SELECT * FROM Reminder")
    @Transaction
     fun getReminders(): LiveData<List<ReminderAndRemindTime>>

    @Query("SELECT * FROM Reminder WHERE reminderId = :id")
    @Transaction
     fun getReminderById(id: String): ReminderAndRemindTime

    @Insert
     fun addReminder(reminder: Reminder): Unit

}