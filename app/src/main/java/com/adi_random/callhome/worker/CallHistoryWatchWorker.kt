package com.adi_random.callhome.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.adi_random.callhome.R
import com.adi_random.callhome.content.ContentRetriever
import com.adi_random.callhome.database.ReminderRepository
import com.adi_random.callhome.model.Reminder
import com.adi_random.callhome.ui.main.ERROR_NOTIFICATION_CHANNEL
import com.adi_random.callhome.ui.main.MainActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import java.util.*

/**
 * Created by CallHistoryWatchWorker on 17-Aug-20
 */

class CallHistoryWatchWorker(
    private val appCtx: Context,
    workerParams: WorkerParameters
) :
    CoroutineWorker(appCtx, workerParams) {

    private var contentRetriever = ContentRetriever(appCtx)

    @TestOnly
    fun setContentRetriever(value: ContentRetriever) {
        contentRetriever = value
    }

    private var repository = ReminderRepository.getInstance(appCtx)

    @TestOnly
    fun setRepository(value: ReminderRepository) {
        repository = value
    }

    private var _test = false

    @TestOnly
    fun setTestMode() {
        _test = true
    }

    private var dispatcher = Dispatchers.IO

    @TestOnly
    fun setDispatcher(value: CoroutineDispatcher) {
        dispatcher = value
    }

    override suspend fun doWork(): Result {

        val reminders = repository.getReminders().map(Reminder::fromReminderAndRemindTime)
        for (reminder in reminders) {
            GlobalScope.launch(dispatcher) {
//                    Check the error count
//                TODO: Remove the test id
                if (reminder.errorCount < 5 || reminder.reminderId == 2087592708816916333) {
// Notify user to delete reminder

                    val intent = Intent(appCtx, MainActivity::class.java).apply {
                        putExtra(MainActivity.ReminderErrorIdExtra, reminder.reminderId)
                    }
                    val pendingIntent = PendingIntent.getActivity(appCtx, 0, intent, 0)

                    val notificationBuilder =
                        NotificationCompat.Builder(appCtx, ERROR_NOTIFICATION_CHANNEL)
//                                TODO: Change icon
                            .setSmallIcon(R.drawable.ic_baseline_contacts_24)
                            .setContentTitle("Reminder error")
                            .setContentText("There seems to be an error with one of your reminders")
                            .setStyle(
                                NotificationCompat.BigTextStyle()
                                    .bigText("There seems to be an error with one of your reminders. We recommend deleting and recreating it to continue getting that reminder")
                            )
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)

//                        Send notification
                    with(NotificationManagerCompat.from(appCtx)) {
                        notify(Random().nextInt(), notificationBuilder.build())
                    }

                }

                //Even if there were errors until now, attempt to handle the reminder

                //Get the phone number associated with this reminder

                //Get the date of the last placed call to that number
                try {
                    val date = contentRetriever.getLastCallDate(reminder.contact)

                    for (remindTime in reminder.timesToRemind) {
                        launch {
                            val nextCallDate =
                                remindTime.calculateNextExecution(
                                    reminder.lastCallDate
                                )
                            if (date != null && date >= nextCallDate) {
                                //  Next call was made
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
                } catch (e: Error) {
                    if (_test) {
                        reminder.countError(repository, dispatcher)
                    } else
                        reminder.countError(appCtx)
                }

            }
        }

        return Result.success()
    }
}
