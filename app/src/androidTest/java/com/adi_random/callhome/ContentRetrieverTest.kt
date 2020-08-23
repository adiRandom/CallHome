package com.adi_random.callhome

import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.model.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@MediumTest
class ContentRetrieverTest {
    private val dispatcher = TestCoroutineDispatcher()
    private val contentRetriever = ContentRetriever(InstrumentationRegistry.getInstrumentation().context,dispatcher)
    @Test
    fun retrieveThePhoneNumberOfAContact() = runBlockingTest  {
        val ids = contentRetriever.getAllContactsIds()
        val numbers = emptyList<Contact>().toMutableList()
        for(id in ids){
            numbers+=contentRetriever.getContact(id)
        }
        MatcherAssert.assertThat(ids, Matchers.hasSize(1))
    }
}