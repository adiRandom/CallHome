package com.adi_random.callhome.model

import com.adi_random.callhome.util.RemindTime
import java.util.*

/**
 * Created by Adrian Pascu on 19-Aug-20
 */


class Reminder(
    val contact: Contact,
    var timesToRemind: List<RemindTime>, var lastCallDate: Date
) {


    fun madeCall(date: Date) {
//        Update last call made
        lastCallDate = date
    }


    companion object {
        const val JOB_KEY = "reminders"
    }

}