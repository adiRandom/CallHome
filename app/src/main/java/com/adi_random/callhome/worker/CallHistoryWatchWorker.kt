package com.adi_random.callhome.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.model.Reminder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by CallHistoryWatchWorker on 17-Aug-20
 */
class CallHistoryWatchWorker(
    private val appCtx: Context,
    workerParams: WorkerParameters
) :
    CoroutineWorker(appCtx, workerParams) {
    override suspend fun doWork(): Result {
        val contentRetriever = ContentRetriever(appCtx)
        val reminders: List<Reminder>? = inputData.keyValueMap[Reminder.JOB_KEY] as List<Reminder>?
        if (reminders != null)
            for (reminder in reminders) {
                GlobalScope.launch {

//                    Check the error count
                    if (reminder.errorCount >= 5) {
                        //                        TODO: Notify user to delete reminder
                    }

                    //Get the phone number associated with this reminder

                    //Get the date of the last placed call to that number
                    val date = contentRetriever.getLastCallDate(reminder.contact)

                    if (date != null) {
                        for (remindTime in reminder.timesToRemind) {
                            launch {
                                val nextCallDate =
                                    remindTime.calculateNextExecution(
                                        appCtx,
                                        reminder.lastCallDate
                                    )
                                if (date >= nextCallDate) {
                                    //                    Next call was made
                                    reminder.madeCall(date)

                                } else if (Date() >= nextCallDate) {
                                    val calendar = Calendar.getInstance()
//                        Get what day is today
                                    val today = calendar.get(Calendar.DAY_OF_MONTH)

                                    //Get the day the next call should be placed
                                    calendar.time = nextCallDate
                                    val nextCallDay = calendar.get(Calendar.DAY_OF_MONTH)

                                    if (today == nextCallDay) {
                                        //The next call should be placed
//                            TODO: Notify user to place call
                                    } else {
                                        //The next call was missed
//                            TODO: Notify user they missed to place a call
                                    }
                                }

                            }

                        }
                    } else {
//                        Reminder error
                        reminder.countError(appCtx)
                    }

                }
            }

        return Result.success()
    }
}
