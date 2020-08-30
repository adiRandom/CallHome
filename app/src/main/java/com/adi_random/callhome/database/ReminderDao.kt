package com.adi_random.callhome.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.adi_random.callhome.database.models.ReminderAndRemindTime
import com.adi_random.callhome.model.Reminder


/**
 * Created by Adrian Pascu on 26-Aug-20
 */
@Dao
interface ReminderDao {

    @Query("SELECT * FROM Reminder ORDER BY creationDate DESC")
    @Transaction
    fun getRemindersAsLiveData(): LiveData<List<ReminderAndRemindTime>>

    @Query("SELECT * FROM Reminder ")
    @Transaction
    fun getReminders(): List<ReminderAndRemindTime>

    @Query("SELECT * FROM Reminder WHERE reminderId = :id")
    @Transaction
    fun getReminderById(id: Long): ReminderAndRemindTime

    @Insert
    fun addReminder(reminder: Reminder)

    @Update
    fun updateReminder(reminder: Reminder)

    @Query("UPDATE Reminder SET errorCount = errorCount + 1 WHERE reminderId = :reminderId")
    fun countError(reminderId: Long)

    @Delete
    fun deleteReminder(reminder: Reminder)

}