package com.adi_random.callhome.content

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import com.adi_random.callhome.model.Contact
import com.adi_random.callhome.model.EMPTY_CONTACT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*


/**
 * Created by Adrian Pascu on 19-Aug-20
 */
class ContentRetriever(ctx: Context, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private var contentResolver: ContentResolver = ctx.contentResolver

    suspend fun getContact(id: Long): Contact = withContext(dispatcher) {

        val contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val contactProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )
        val contactSelectionClause = "${ContactsContract.CommonDataKinds.Phone._ID} = ?"
        val contactSelectionArg = arrayOf(id.toString())

        val contactCursor = contentResolver.query(
            contactUri,
            contactProjection,
            contactSelectionClause,
            contactSelectionArg,
            null
        )

        try {
            when (contactCursor?.count) {
                null -> {
//                    TODO: Add error
                    contactCursor?.close()
                    return@withContext EMPTY_CONTACT
                }
                0 -> {
//                    TODO: Count fails
                    contactCursor.close()
                    return@withContext EMPTY_CONTACT
                }
                else -> {
                    return@withContext contactCursor.run {
                        val contactIndex =
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                        val nameIndex =
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        var number: String
                        moveToNext()
//                        The phone number and name for this id
                        number = getString(contactIndex)
                        val name = getString(nameIndex)
                        close()
                        Contact(id, name, number)
                    }
                }
            }
        } catch (e: Error) {
//            TODO:Add error
            contactCursor?.close()
            return@withContext EMPTY_CONTACT
        }
    }


    suspend fun getLastCallDate(contact: Contact): Date = withContext(dispatcher) {


        //Get the call history for this number
        val logUri = CallLog.CONTENT_URI
        val logProjection = arrayOf(
            CallLog.Calls.DATE,
            CallLog.Calls.TYPE,
            CallLog.Calls.CACHED_NORMALIZED_NUMBER
        )
        val logSelectionClause = "${CallLog.Calls.NUMBER} = ?"
        val logSelectionArg = arrayOf(contact.phoneNumber)
        val sorting = "${CallLog.Calls.DATE} DESC"
        val logCursor = contentResolver.query(
            logUri,
            logProjection,
            logSelectionClause,
            logSelectionArg,
            sorting
        )

        try {
            when (logCursor?.count) {
                null -> {
//                                    TODO: Add error
                    logCursor?.close()
                    return@withContext Date(0)
                }
                0 -> {
//                                            TODO: Count fail lookup
                    logCursor.close()
                    return@withContext Date(0)
                }

                else -> {
                    val dateIndex =
                        logCursor.getColumnIndex(CallLog.Calls.DATE)
                    logCursor.moveToNext()
                    logCursor.close()
                    return@withContext Date(logCursor.getLong(dateIndex))
                }

            }
        } catch (e: java.lang.Error) {
//                                    TODO: Add error
            logCursor?.close()
            return@withContext Date(0)
        }

    }

    suspend fun getAllContactsIds(): List<Long> = withContext(dispatcher) {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone._ID)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        try {
            when (cursor?.count) {
                null -> {
                    println("Null")
                    cursor?.close(); emptyList<Long>()

                }
                0 -> {
                    println("Got nothing")
                    cursor.close(); emptyList<Long>()
                }
                else -> {
                    println("Got something")
                    cursor.let {
                        val index =
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
                        val ids = emptyList<Long>().toMutableList()
                        while (cursor.moveToNext()) {
                            ids += cursor.getLong(index)
                        }
                        cursor.close()
                        ids
                    }
                }
            }

        } catch (e: Error) {
            cursor?.close()
            emptyList<Long>()
        }
    }

    suspend fun getContactIdFromUri(uri: Uri): Long = withContext(dispatcher) {
        val projection = arrayOf(ContactsContract.Contacts._ID)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        try {
            when (cursor?.count) {
                null -> {
//                    TODO: Handle error
                    cursor?.close(); 0L

                }
                0 -> {
//                    TODO: Handle error
                    cursor.close(); 0L
                }
                else -> {
                    println("Got something")
                    cursor.let {
                        val index =
                            cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone._ID
                            )
                        cursor.moveToNext()
                        val id = cursor.getLong(index)
                        cursor.close()
                        id
                    }
                }
            }

        } catch (e: Error) {
            cursor?.close()
//            TODO: Handle error
            0L
        }
    }

}