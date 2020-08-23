package com.adi_random.callhome.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.model.Reminder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                    //Get the phone number associated with this reminder
                    val contact = contentRetriever.getContact(reminder.getId())

                    //Get the date of the last placed call to that number
                    val date = contentRetriever.getLastCallDate(contact)
                    if (date > reminder.nextCallDate) {
//                                                TODO: Notify user
                    } else if (date == reminder.nextCallDate) {
//                                                TODO: Notify user
                    } else {
                        reminder.madeCall(date)
                    }
                }

            }

        return Result.success()
    }
}