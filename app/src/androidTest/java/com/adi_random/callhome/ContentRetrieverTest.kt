package com.adi_random.callhome

import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.model.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.junit.Test

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
            val contact = contentRetriever.getContact(id)
            if (contact != null)
                numbers += contact
        }
        assertThat(ids, hasSize(2))
    }

    @Test
    fun getContactImage() = runBlockingTest {
        val ids = contentRetriever.getAllContactsIds()
        val contact = contentRetriever.getContact(ids[0])
        assertThat(contact?.photo, notNullValue())
    }




}