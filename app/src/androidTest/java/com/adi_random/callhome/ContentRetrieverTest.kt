package com.adi_random.callhome

import android.graphics.Bitmap
import androidx.room.Room
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.database.Database
import com.adi_random.callhome.database.ReminderRepository
import com.adi_random.callhome.model.Contact
import com.adi_random.callhome.model.ReminderBuilder
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@LargeTest
class ContentRetrieverTest {
    private val dispatcher = TestCoroutineDispatcher()
    private val context = InstrumentationRegistry.getInstrumentation().context
    private val contentRetriever = ContentRetriever(context, dispatcher)

    @Test
    fun retrieveThePhoneNumberOfAContact() = runBlockingTest {
        val ids = contentRetriever.getAllContactsIds()
        val numbers = emptyList<Contact>().toMutableList()
        for (id in ids) {
            numbers += contentRetriever.getContact(id)
        }
        assertThat(ids, hasSize(1))
    }

    @Test
    fun getContactImage() = runBlockingTest {
        val ids = contentRetriever.getAllContactsIds()
        val contact = contentRetriever.getContact(ids[0])
        assertThat(contact.photo, notNullValue())
    }

    @Test
    fun preserveContactImageInDatabase() = runBlockingTest {
        val db =
            Room.inMemoryDatabaseBuilder(context, Database.BasicRoomDatabase::class.java).build()
        val repository = ReminderRepository.getInstance(db)

        val ids = contentRetriever.getAllContactsIds()
        val contact = contentRetriever.getContact(ids[0])

//        Get the base 64 representation of the picture from the content resolver for reference
        var byteArray = ByteArrayOutputStream()
        contact.photo?.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
        val photoBase64Representation = Base64.getEncoder().encodeToString(byteArray.toByteArray())


//        Create a reminder with this contact to save it to the DB
        val reminder = ReminderBuilder(context, dispatcher)
            .withReminderType(ReminderType.WEEKLY)
            .withContact(contact)
            .withTimesToRemind(listOf(1))
            .build()

        repository.insertReminder(reminder)

        //Get reminder and compare pictures

        val dbPicture = repository.getReminderById(reminder.reminderId).reminder.contact.photo
        byteArray = ByteArrayOutputStream()
        dbPicture?.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
        val dbPhotoBase64Representation =
            Base64.getEncoder().encodeToString(byteArray.toByteArray())

        assertThat(dbPhotoBase64Representation, `is`(photoBase64Representation))
    }
}