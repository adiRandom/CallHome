package com.adi_random.callhome.model

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Created by Adrian Pascu on 19-Aug-20
 */


class Reminder(
    private var id: Long,// Frequency is a crontab
    private var frequency: String, var lastCallDate: Date
) {

    fun getId() = this.id

    var nextCallDate: Date = Date()

    init {
        this.nextCallDate = getNextExecutionDate()
    }

    fun madeCall(date: Date) {
//        Update last call made
        lastCallDate = date

        //Get next execution date
        nextCallDate = getNextExecutionDate()

    }

    private fun getNextExecutionDate(): Date {
        val cronDef = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)
        val parser = CronParser(cronDef)
        val execTime = ExecutionTime.forCron(parser.parse(frequency))
        val lastExecution =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastCallDate.time), ZoneId.systemDefault())
        val nextExecution = execTime.nextExecution(lastExecution)
        return Date.from(nextExecution.get().toInstant())
    }
companion object{
    const val JOB_KEY = "reminders"
}

}