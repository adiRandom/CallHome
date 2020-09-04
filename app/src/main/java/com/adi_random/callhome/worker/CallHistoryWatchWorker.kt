package com.adi_random.callhome.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri

import android.util.Log
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
import com.adi_random.callhome.ui.main.REMINDERS_NOTIFICATION_CHANNEL
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

        Log.d("Worker status", "Running")

        val reminders = repository.getReminders().map(Reminder::fromReminderAndRemindTime)
        for (reminder in reminders) {
            GlobalScope.launch(dispatcher) {
//                    Check the error count
                if (reminder.errorCount >= 5) {
// Notify user to delete reminder

                    val intent = Intent(appCtx, MainActivity::class.java).apply {
                        putExtra(MainActivity.ReminderErrorIdParam, reminder.reminderId)
                    }
                    val pendingIntent = PendingIntent.getActivity(appCtx, 0, intent, 0)

                    var notificationBuilder =
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

//                        Send notification
                    with(NotificationManagerCompat.from(appCtx)) {
                        notify(Random().nextInt(), notificationBuilder.build())
                    }

                }

                //Even if there were errors until now, attempt to handle the reminder

                //Get the phone number associated with this reminder

                //Get the date of the last placed call to that number
                try {
                    val lastCallPlaced = contentRetriever.getLastCallDate(reminder.contact)

                    for (remindTime in reminder.timesToRemind) {
                        launch {
                            val nextCallDate =
                                remindTime.calculateNextExecution(
                                    reminder.lastCallDate
                                )
                            if (lastCallPlaced != null && lastCallPlaced >= nextCallDate) {
                                //  Next call was made
                                reminder.madeCall(lastCallPlaced)
//                                Update the database
                                repository.callMade(reminder.reminderId, lastCallPlaced)

                            } else if (Date() /*now*/ >= nextCallDate) {

//                                val calendar = Calendar.getInstance()
////                        Get what day is today
//                                val today = calendar.get(Calendar.DAY_OF_MONTH)
//
//                                //Get the day the next call should be placed
//                                calendar.time = nextCallDate
//                                val nextCallDay = calendar.get(Calendar.DAY_OF_MONTH)

//                                Calculate dif between the day the next call should be placed and today.
//                                If dif smaller than 24h, the call should be placed today

                                if (Date().time - nextCallDate.time <= 86400000 /*Number of ms in 24h*/) {
                                    //The next call should be placed
//                                    Create a intent to show the phone dialer
                                    val intent = Intent(
                                        Intent.ACTION_DIAL,
                                        Uri.parse("tel:${reminder.contact.phoneNumber}")
                                    )
                                    val pendingIntent =
                                        PendingIntent.getActivity(applicationContext, 0, intent, 0)

                                    val notificationGroup = "REMINDERS_GROUP"

                                    val notificationBuilder = NotificationCompat.Builder(
                                        applicationContext,
                                        REMINDERS_NOTIFICATION_CHANNEL
                                    )
                                        .setSmallIcon(R.drawable.ic_baseline_contacts_24)
                                        .setContentTitle("Make a call")
                                        .setContentText("It's time to call ${reminder.contact.name}")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setGroup(notificationGroup)
                                        .setContentIntent(pendingIntent)
                                    with(NotificationManagerCompat.from(applicationContext)) {
                                        notify(Random().nextInt(), notificationBuilder.build())
                                    }

                                } else {
                                    //The next call was missed
//                                    Create a intent to show the phone dialer
                                    val intent = Intent(
                                        Intent.ACTION_DIAL,
                                        Uri.parse("tel:${reminder.contact.phoneNumber}")
                                    )
                                    val pendingIntent =
                                        PendingIntent.getActivity(applicationContext, 0, intent, 0)

                                    val notificationGroup = "REMINDERS_GROUP"

                                    val notificationBuilder = NotificationCompat.Builder(
                                        applicationContext,
                                        REMINDERS_NOTIFICATION_CHANNEL
                                    )
                                        .setSmallIcon(R.drawable.ic_baseline_contacts_24)
                                        .setContentTitle("Make a call")
                                        .setContentText("You missed your last call to ${reminder.contact.name}")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setGroup(notificationGroup)
                                        .setContentIntent(pendingIntent)
                                    with(NotificationManagerCompat.from(applicationContext)) {
                                        notify(Random().nextInt(), notificationBuilder.build())
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Error) {
                    if (_test) {
                        reminder.countError(appCtx, repository, dispatcher)
                    } else
                        reminder.countError(appCtx)
                }

            }
        }

        return Result.success()
    }
}
