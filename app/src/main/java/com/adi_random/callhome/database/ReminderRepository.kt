package com.adi_random.callhome.database

import android.content.Context
import com.adi_random.callhome.model.Reminder
import org.jetbrains.annotations.TestOnly


/**
 * Created by Adrian Pascu on 26-Aug-20
 */
class ReminderRepository {

    private val db: Database.BasicRoomDatabase

    private constructor(ctx: Context) {
        db = Database.getInstance(ctx)
    }

    private constructor(_db: Database.BasicRoomDatabase) {
        db = _db
    }

    fun insertReminder(reminder: Reminder) {
        db.remindTimeDao().insertAll(reminder.timesToRemind)
        db.reminderDao().addReminder(reminder)
    }

    fun getReminders() =
        db.reminderDao().getReminders()


    fun getReminderById(id: String) = db.reminderDao().getReminderById(id)

    companion object {
        private lateinit var repo: ReminderRepository
        fun getInstance(ctx: Context): ReminderRepository {
            if (!this::repo.isInitialized) {
                repo = ReminderRepository(ctx)
            }
            return repo
        }

        @TestOnly
        fun getInstance(db: Database.BasicRoomDatabase): ReminderRepository = ReminderRepository(db)
    }
}