package com.adi_random.callhome.database

import androidx.room.TypeConverter
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import com.cronutils.model.Cron
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
import java.util.*


/**
 * Created by Adrian Pascu on 26-Aug-20
 */
class TypeConverters {

    @TypeConverter
    fun fromReminderTypeToInt(value: ReminderType): Int = value.value

    @TypeConverter
    fun fromIntToReminderType(value: Int): ReminderType = ReminderType.getReminderTypeFromInt(value)

    @TypeConverter
    fun fromCronToString(cron: Cron): String = cron.asString()

    @TypeConverter
    fun fromStringToCron(string: String): Cron {
        val cronParser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
        return cronParser.parse(string)
    }

    @TypeConverter
    fun fromDateToLong(value: Date):Long = value.time

    @TypeConverter
    fun fromLongToDate(value:Long):Date = Date(value)

}