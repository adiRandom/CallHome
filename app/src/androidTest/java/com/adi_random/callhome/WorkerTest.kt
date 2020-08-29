package com.adi_random.callhome

import androidx.room.Room
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.testing.TestListenableWorkerBuilder
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.database.Database
import com.adi_random.callhome.database.ReminderRepository
import com.adi_random.callhome.model.Reminder
import com.adi_random.callhome.model.ReminderBuilder
import com.adi_random.callhome.ui.main.MainActivity
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import com.adi_random.callhome.worker.CallHistoryWatchWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.spy


/**
 * Created by Adrian Pascu on 28-Aug-20
 */

@ExperimentalCoroutinesApi
@LargeTest
class WorkerTest {
    private val dispatcher = TestCoroutineDispatcher()
    private val context = InstrumentationRegistry.getInstrumentation().context
    private val contentRetriever = ContentRetriever(context, dispatcher)

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun checkNoErrorCountUponSuccessfulRetrieval() = runBlockingTest {


        val contactsIds = contentRetriever.getAllContactsIds()
        val contact1 = contentRetriever.getContact(contactsIds[0])
        val contact2 = contentRetriever.getContact(contactsIds[1])

//        Create some reminders

        val reminder1 = ReminderBuilder(context, dispatcher)
            .withReminderType(ReminderType.WEEKLY)
            .withContact(contact1)
            .withTimesToRemind(listOf(2))
            .build()
        val reminder2 = ReminderBuilder(context, dispatcher)
            .withReminderType(ReminderType.WEEKLY)
            .withContact(contact2)
            .withTimesToRemind(listOf(2))
            .build()

        MatcherAssert.assertThat(reminder1, notNullValue())
        MatcherAssert.assertThat(reminder2, notNullValue())


        val db =
            Room.inMemoryDatabaseBuilder(context, Database.BasicRoomDatabase::class.java).build()
        val repository = ReminderRepository.getInstance(db)

        repository.insertReminder(reminder1!!)
        repository.insertReminder(reminder2!!)

        //Create the worker
        val worker = TestListenableWorkerBuilder<CallHistoryWatchWorker>(context)
            .build()
        worker.setContentRetriever(contentRetriever)


        worker.setRepository(repository)
        for (i in 1..30) {
            //Run the job 30 times and expect no errors

            val response = worker.doWork()
            //Get the updated variants from the database
            val (dbReminder1, dbReminder2) = repository.getReminders()
                .map(Reminder::fromReminderAndRemindTime)
            MatcherAssert.assertThat(dbReminder1.errorCount, `is`(0))
            MatcherAssert.assertThat(dbReminder2.errorCount, `is`(0))

        }
    }

    @Test
    fun retrievalError() = runBlockingTest {
        val contactsIds = contentRetriever.getAllContactsIds()
        val contact1 = contentRetriever.getContact(contactsIds[0])
        val contact2 = contentRetriever.getContact(contactsIds[1])

//        Create some reminders

        val reminder1 = ReminderBuilder(context, dispatcher)
            .withReminderType(ReminderType.WEEKLY)
            .withContact(contact1)
            .withTimesToRemind(listOf(2))
            .build()
        val reminder2 = ReminderBuilder(context, dispatcher)
            .withReminderType(ReminderType.WEEKLY)
            .withContact(contact2)
            .withTimesToRemind(listOf(2))
            .build()



        MatcherAssert.assertThat(reminder1, notNullValue())
        MatcherAssert.assertThat(reminder2, notNullValue())


        val db =
            Room.inMemoryDatabaseBuilder(context, Database.BasicRoomDatabase::class.java).build()
        val repository = ReminderRepository.getInstance(db)
        repository.insertReminder(reminder1!!)
        repository.insertReminder(reminder2!!)


        //Create the worker
        val worker = TestListenableWorkerBuilder<CallHistoryWatchWorker>(context)
            .build()

        val _contentRetriever = ContentRetriever(context, dispatcher)
        val spiedContentRetriever = spy(_contentRetriever)
        `when`(spiedContentRetriever.getLastCallDate(contact1!!)).thenThrow(Error("Some error"))
        worker.setContentRetriever(spiedContentRetriever)
        worker.setDispatcher(dispatcher)
        worker.setTestMode()


        worker.setRepository(repository)
        for (i in 1..5) {

            val response = worker.doWork()
            //Get the updated variants from the database
            val (dbReminder1, dbReminder2) = repository.getReminders()
                .map(Reminder::fromReminderAndRemindTime)
            MatcherAssert.assertThat(dbReminder1.errorCount, `is`(i))
            MatcherAssert.assertThat(dbReminder2.errorCount, `is`(0))

        }
    }

    @Test
    fun reminderUpdate() = runBlockingTest {

//        Create a test reminder, add it to the db, change a property on the base reminder,
//        update the db and expect the property to be changed

        val contactsIds = contentRetriever.getAllContactsIds()
        val contact1 = contentRetriever.getContact(contactsIds[0])

//        Create some reminders

        val reminder1 = ReminderBuilder(context, dispatcher)
            .withReminderType(ReminderType.WEEKLY)
            .withContact(contact1)
            .withTimesToRemind(listOf(2))
            .build()



        MatcherAssert.assertThat(reminder1, notNullValue())


        val db =
            Room.inMemoryDatabaseBuilder(context, Database.BasicRoomDatabase::class.java).build()
        val repository = ReminderRepository.getInstance(db)
        repository.insertReminder(reminder1!!)
        reminder1.countError(context, repository, dispatcher)

        val dbReminder = repository.getReminderById(reminder1.reminderId)
        assertThat(dbReminder.reminder.errorCount, `is`(1))
    }

}