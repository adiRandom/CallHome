package com.adi_random.callhome.util

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adi_random.callhome.R
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import com.adi_random.callhome.ui.settings.TimePickerPreference
import com.cronutils.builder.CronBuilder
import com.cronutils.model.Cron
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpression.always
import com.cronutils.model.field.expression.FieldExpression.questionMark
import com.cronutils.model.field.expression.FieldExpressionFactory.on
import com.cronutils.model.time.ExecutionTime
import org.jetbrains.annotations.TestOnly
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


/**
 * Created by Adrian Pascu on 25-Aug-20
 */

@Entity
class RemindTime(
    val timeToRemind: Int,
    val type: ReminderType,
    val reminderId: Long,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) {
    lateinit var cron: Cron

    fun calculateNextExecution(lastCallTime: Date): Date {
        val executionTime = ExecutionTime.forCron(cron)
        val lastExecution =
            ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(lastCallTime.time),
                ZoneId.systemDefault()
            )
        return Date.from(executionTime.nextExecution(lastExecution).get().toInstant())
    }

    override fun toString(): String {
        return when (type) {
            ReminderType.DAILY -> {
                val hour =
                    if (timeToRemind / 100 < 10) "0${timeToRemind / 100}"; else timeToRemind / 100
                val minute =
                    if (timeToRemind % 100 < 10) "0${timeToRemind % 100}"; else timeToRemind % 100
                "At $hour:$minute every day"
            }
            ReminderType.WEEKLY -> {
                val day: String = when (timeToRemind) {
                    1 -> "Monday"
                    2 -> "Tuesday"
                    3 -> "Wednesday"
                    4 -> "Thursday"
                    5 -> "Friday"
                    6 -> "Saturday"
                    7 -> "Sunday"
                    else -> ""
                }
                "Every $day"
            }
            ReminderType.MONTHLY -> {
                val day = when (timeToRemind) {
                    1 -> "1st"
                    2 -> "2nd"
                    3 -> "3rd"
                    21 -> "21st"
                    22 -> "22nd"
                    23 -> "23rd"
                    31 -> "31st"
                    else -> "${timeToRemind}th"
                }
                "The $day of every month"
            }
        }
    }

    fun buildCron(context: Context) {
        cron = when (type) {
            ReminderType.DAILY -> {
                CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                    .withYear(always())
                    .withDoM(always())
                    .withMonth(always())
                    .withDoW(questionMark())
                    .withHour(on(timeToRemind / 100))
                    .withMinute(on(timeToRemind % 100))
                    .withSecond(on(0))
                    .instance()
            }
            ReminderType.WEEKLY -> {

                val calendar = Calendar.getInstance()
                val preference = PreferenceManager.getDefaultSharedPreferences(context).getLong(
                    context.getString(
                        R.string.weekly_reminders_time_key
                    ), TimePickerPreference.getDefaultValue()
                )
                calendar.timeInMillis = preference

                CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                    .withYear(always())
                    .withDoM(questionMark())
                    .withMonth(always())
                    .withDoW(on(timeToRemind))
                    .withHour(
                        on(calendar.get(Calendar.HOUR_OF_DAY))
                    )
                    .withMinute(on(calendar.get(Calendar.MINUTE)))
                    .withSecond(on(0))
                    .instance()
            }
            ReminderType.MONTHLY -> {

                val calendar = Calendar.getInstance()
                val preference = PreferenceManager.getDefaultSharedPreferences(context).getLong(
                    context.getString(
                        R.string.monthly_reminders_time_key
                    ), TimePickerPreference.getDefaultValue()
                )
                calendar.timeInMillis = preference

                CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                    .withYear(always())
                    .withDoM(on(timeToRemind))
                    .withMonth(always())
                    .withDoW(questionMark())
                    .withHour(
                        on(calendar.get(Calendar.HOUR_OF_DAY))
                    )
                    .withMinute(on(calendar.get(Calendar.MINUTE)))
                    .withSecond(on(0))
                    .instance()
            }
        }
    }

    @TestOnly
    fun _getId(): Int {
        return id
    }
}