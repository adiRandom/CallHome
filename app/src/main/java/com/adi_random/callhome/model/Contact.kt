package com.adi_random.callhome.model

import android.graphics.Bitmap


/**
 * Created by Adrian Pascu on 23-Aug-20
 */

open class Contact(val id: Long, val name: String, val phoneNumber: String, val photo: Bitmap?)

val EMPTY_IMAGE:Bitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888)

object EMPTY_CONTACT : Contact(0, "", "", EMPTY_IMAGE)