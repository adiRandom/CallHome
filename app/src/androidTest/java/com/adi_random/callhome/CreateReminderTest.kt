package com.adi_random.callhome

import androidx.room.Room
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.database.Database
import com.adi_random.callhome.database.ReminderRepository
import com.adi_random.callhome.model.Reminder
import com.adi_random.callhome.model.ReminderBuilder
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import com.adi_random.callhome.util.RemindTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Test
import java.util.*

/**
 * Created by Adrian Pascu on 24-Aug-20
 */

@ExperimentalCoroutinesApi
@LargeTest
class CreateReminderTest {
    private val dispatcher = TestCoroutineDispatcher()
    private val context = InstrumentationRegistry.getInstrumentation().context
    private val contentRetriever = ContentRetriever(context, dispatcher)

//    @After
//    fun closeDb() {
//        database.clearAllTables()
//        database.close()
//    }

    @Test
//    Test the creation of a Reminder
    fun createDailyReminder() = runBlockingTest {
        val contactIds = contentRetriever.getAllContactsIds()
        val contact = contentRetriever.getContact(contactIds[0])
        val reminder = ReminderBuilder(context, dispatcher)
            .withContact(contact)
            .withReminderType(ReminderType.DAILY)
            .withTimesToRemind(listOf(900))
            .build()
        val expectedCalendar = Calendar.getInstance()
        expectedCalendar.time = Date(1598358060000)
        val calendar = Calendar.getInstance()
        calendar.time = reminder.lastCallDate
        assertThat(
            calendar.get(Calendar.DAY_OF_MONTH),
            `is`(expectedCalendar.get(Calendar.DAY_OF_MONTH))
        )
    }

    @Test
//    Create 2 reminders and see if they get added correctly to the database
    fun addRemindersToDb() = runBlockingTest {

        //Get database
        val database =
            Room.inMemoryDatabaseBuilder(context, Database.BasicRoomDatabase::class.java).build()
        val repository = ReminderRepository.getInstance(database)

        //Get the contact
        val contactIds = contentRetriever.getAllContactsIds()
        val contact = contentRetriever.getContact(contactIds[0])

        //Create a weekly and a daily reminder

        val reminderBuilder = ReminderBuilder(context, dispatcher)

        val weeklyReminderId = UUID.randomUUID().toString()
        val dailyReminderId = UUID.randomUUID().toString()

//        Monday and friday
        val weeklyReminderTimesToRemindInt = listOf<Int>(1, 5)
        val weeklyReminderTimesToRemind = weeklyReminderTimesToRemindInt.map {
            RemindTime(
                it,
                ReminderType.WEEKLY,
                weeklyReminderId
            )
        }

//        9:30 and 10:45
        val dailyReminderTimesToRemindInt = listOf<Int>(930, 1045)
        val dailyReminderTimesToRemind = weeklyReminderTimesToRemindInt.map {
            RemindTime(
                it,
                ReminderType.DAILY,
                dailyReminderId
            )
        }


        val weeklyReminder = reminderBuilder
            .withReminderType(ReminderType.WEEKLY)
            .withContact(contact)
            .withTimesToRemind(weeklyReminderTimesToRemindInt)
            .withId(weeklyReminderId)
            .build()

        val dailyReminder = reminderBuilder
            .withReminderType(ReminderType.DAILY)
            .withContact(contact)
            .withTimesToRemind(dailyReminderTimesToRemindInt)
            .withId(dailyReminderId)
            .build()

        //Insert the reminders in the database, then retrieve them and compare the 2

        repository.insertReminder(weeklyReminder)
        repository.insertReminder(dailyReminder)

        val dbWeeklyReminder = Reminder.fromReminderAndRemindTime(
            repository.getReminderById(weeklyReminderId)
        )
        val dbDailyReminder = Reminder.fromReminderAndRemindTime(
            repository.getReminderById(dailyReminderId)
        )

        //Compare contact

        assertThat(dbWeeklyReminder.contact.id, `is`(weeklyReminder.contact.id))
        assertThat(dbDailyReminder.contact.id, `is`(dailyReminder.contact.id))

        //Compare last call date

        assertThat(dbWeeklyReminder.lastCallDate, `is`(weeklyReminder.lastCallDate))
        assertThat(dbDailyReminder.lastCallDate, `is`(dailyReminder.lastCallDate))

        //Compare times to remind

        assertThat(dbDailyReminder.timesToRemind.size, `is`(dailyReminder.timesToRemind.size))
        assertThat(dbWeeklyReminder.timesToRemind.size, `is`(weeklyReminder.timesToRemind.size))

        assertThat(
            dbDailyReminder.timesToRemind[0].cron.asString(),
            `is`(dailyReminder.timesToRemind[0].cron.asString())
        )
        assertThat(
            dbWeeklyReminder.timesToRemind[0].cron.asString(),
            `is`(weeklyReminder.timesToRemind[0].cron.asString())
        )

        assertThat(
            dbDailyReminder.timesToRemind[1].cron.asString(),
            `is`(dailyReminder.timesToRemind[1].cron.asString())
        )
        assertThat(
            dbWeeklyReminder.timesToRemind[1].cron.asString(),
            `is`(weeklyReminder.timesToRemind[1].cron.asString())
        )

        assertThat(dbDailyReminder.timesToRemind[0]._getId(), not(0))
        assertThat(dbDailyReminder.timesToRemind[1]._getId(), not(0))
        assertThat(dbWeeklyReminder.timesToRemind[0]._getId(), not(0))
        assertThat(dbWeeklyReminder.timesToRemind[1]._getId(), not(0))
    }
}