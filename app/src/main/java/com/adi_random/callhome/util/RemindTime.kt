package com.adi_random.callhome.util

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adi_random.callhome.ui.main.addreminder.ReminderType
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
    val reminderId: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) {

    var cron: Cron = when (type) {
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
            CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                .withYear(always())
                .withDoM(questionMark())
                .withMonth(always())
                .withDoW(on(timeToRemind))
                .withHour(on(0))
//                        TODO: Fetch hour and minute from settings
                .withMinute(on(0))
                .withSecond(on(0))
                .instance()
        }
        ReminderType.MONTHLY -> {
            CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                .withYear(always())
                //                        TODO: Fetch hour and minute from settings
                .withDoM(on(timeToRemind))
                .withMonth(always())
                .withDoW(questionMark())
                .withHour(on(0))
                .withMinute(on(0))
                .withSecond(on(0))
                .instance()
        }
    }

    fun calculateNextExecution(lastCallTime: Date): Date {
        val executionTime = ExecutionTime.forCron(cron)
        val lastExecution =
            ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(lastCallTime.time),
                ZoneId.systemDefault()
            )
        return Date.from(executionTime.nextExecution(lastExecution).get().toInstant())
    }

    @TestOnly
    fun _getId(): Int {
        return id
    }
}