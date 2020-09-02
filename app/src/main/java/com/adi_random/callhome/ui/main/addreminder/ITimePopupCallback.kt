package com.adi_random.callhome.ui.main.addreminder


/**
 * Created by Adrian Pascu on 24-Aug-20
 */
interface ITimePopupCallback {
    fun onTimePicked(hour: Int, minute: Int)

    //    1-7 for day of the week
//    1-31 for day of the month
    fun onDayPicked(day: Int)
}