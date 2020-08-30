package com.adi_random.callhome.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.adi_random.callhome.model.Reminder
import com.adi_random.callhome.util.RemindTime


/**
 * Created by Adrian Pascu on 26-Aug-20
 */


class Database private constructor() {
    @androidx.room.Database(
        entities = [Reminder::class, RemindTime::class],
        version = 7,
        exportSchema = false,
    )

    @TypeConverters(com.adi_random.callhome.database.TypeConverters::class)
    abstract class BasicRoomDatabase : RoomDatabase() {
        abstract fun reminderDao(): ReminderDao
        abstract fun remindTimeDao(): RemindTimeDao
        protected fun finalize() {
            close()
        }
    }

    companion object {
        private const val dbName = "CallHomeDatabase"
        private lateinit var db: BasicRoomDatabase
        fun getInstance(context: Context): BasicRoomDatabase {
            if (!this::db.isInitialized) {
                db = Room.databaseBuilder(context, BasicRoomDatabase::class.java, dbName)
                    .fallbackToDestructiveMigration().build()
            }
            return db
        }
    }
}