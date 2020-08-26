package com.adi_random.callhome.model


/**
 * Created by Adrian Pascu on 23-Aug-20
 */

open class Contact( val id:Long, val name:String, val phoneNumber:String)

object EMPTY_CONTACT: Contact(0,"","")