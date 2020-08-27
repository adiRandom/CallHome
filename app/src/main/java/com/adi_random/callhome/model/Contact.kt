package com.adi_random.callhome.model

import android.graphics.Bitmap


/**
 * Created by Adrian Pascu on 23-Aug-20
 */

open class Contact(val id: Long, val name: String, val phoneNumber: String, val photo: Bitmap?) {
    override operator fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other !is Contact -> false
            other.id != id -> false
            else -> true
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + (photo?.hashCode() ?: 0)
        return result
    }
}

val EMPTY_IMAGE: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

val EMPTY_CONTACT = Contact(0, "", "", EMPTY_IMAGE)