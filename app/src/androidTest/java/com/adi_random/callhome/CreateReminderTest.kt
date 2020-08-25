package com.adi_random.callhome

import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.model.ReminderBuilder
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
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

    @Test
//    Test the creation of a Reminder
    fun createDailyReminder() = runBlockingTest {
        val contactIds = contentRetriever.getAllContactsIds()
        val contact = contentRetriever.getContact(contactIds[0])
        val reminder = ReminderBuilder(context,dispatcher)
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
}