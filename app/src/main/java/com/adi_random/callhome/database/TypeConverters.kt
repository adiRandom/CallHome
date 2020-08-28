package com.adi_random.callhome.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.adi_random.callhome.ui.main.addreminder.ReminderType
import com.cronutils.model.Cron
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
import java.io.ByteArrayOutputStream
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
    fun fromDateToLong(value: Date): Long = value.time

    @TypeConverter
    fun fromLongToDate(value: Long): Date = Date(value)

    @TypeConverter
    fun fromBitmapToString(value: Bitmap?): String {
        if (value == null)
            return ""
        val byteArray = ByteArrayOutputStream()
        value.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
        return Base64.getEncoder().encodeToString(byteArray.toByteArray())
    }

    @TypeConverter
    fun fromStringToBitmap(value: String?): Bitmap? {
        if (value == null || value == "")
            return null
        val bytes = Base64.getDecoder().decode(value)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}