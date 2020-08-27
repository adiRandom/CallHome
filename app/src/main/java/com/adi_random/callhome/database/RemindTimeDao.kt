package com.adi_random.callhome.database

import androidx.room.Dao
import androidx.room.Insert
import com.adi_random.callhome.util.RemindTime


/**
 * Created by Adrian Pascu on 26-Aug-20
 */

@Dao
interface RemindTimeDao {
    @Insert
    fun insertAll(values: List<RemindTime>)
}