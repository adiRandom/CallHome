package com.adi_random.callhome

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import com.adi_random.callhome.ui.main.addreminder.ARG_TYPE
import com.adi_random.callhome.ui.main.addreminder.AddReminderFragment
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

/**
 * Created by Adrian Pascu on 24-Aug-20
 */

@LargeTest
class CreateReminder {
    @Test
//    Test picking a day from a weekly reminder time picker
    fun pickTimeDefaultValue(){
        val fragmentArgs = Bundle().apply {
            putInt(ARG_TYPE,ReminderType.WEEKLY.value)
        }
        val scenario = launchFragmentInContainer<AddReminderFragment>(fragmentArgs)
//        Open the AlertDialog
        onView(withId(R.id.add_time_button)).perform(click())
//        Click the done button
        onView(withText("OK")).perform(click())

        //Test the viewmodel
        scenario.onFragment {
            val viewModel = it._getViewModel()
//            Check the first element of the list
            assertThat(viewModel.timesToRemind.value?.get(0), `is`("1"))
        }
    }
}