package com.adi_random.callhome.content

import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import com.adi_random.callhome.model.Contact
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.TestOnly
import java.io.IOException
import java.io.InputStream
import java.util.*


/**
 * Created by Adrian Pascu on 19-Aug-20
 */
open class ContentRetriever(
    ctx: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private var contentResolver: ContentResolver = ctx.contentResolver

    suspend fun getContact(id: Long): Contact? = withContext(dispatcher) {

        val contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val contactProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
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
                    contactCursor?.close()
                    return@withContext null
                }
                0 -> {
                    contactCursor.close()
                    return@withContext null
                }
                else -> {
                    return@withContext contactCursor.run {
                        val normalizedNumberIndex =
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                        val numberIndex =
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val nameIndex =
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        val photoUriIndex =
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                        moveToNext()
//                        The fields of this contact

//                        If the number is standard invalid, get the number as the user typed it
                        val number = getString(normalizedNumberIndex) ?: getString(numberIndex)
//                        If there is no name, set the number as the contact name
                        val name = getString(nameIndex) ?: number
                        val photoUri = getString(photoUriIndex)
                        val photoStream = getContactPhoto(Uri.parse(photoUri ?: ""))
                        val photo = BitmapFactory.decodeStream(photoStream)
//                        Close the cursor
                        close()
                        Contact(id, name, number, photo)
                    }
                }
            }
        } catch (e: Error) {
            contactCursor?.close()
            return@withContext null
        }
    }

    private fun getContactPhoto(uri: Uri): InputStream? {
        return try {
            contentResolver.openAssetFileDescriptor(uri, "r")?.createInputStream()
        } catch (e: IOException) {
            null
        }
    }


    /**
     * @return Date if a valid last call was found, null otherwise
     */
    @Throws(Error::class)
    open suspend fun getLastCallDate(contact: Contact): Date? = withContext(dispatcher) {


        //Get the call history for this number
        val logUri = CallLog.Calls.CONTENT_URI
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
                    logCursor?.close()
                    throw Error("Error retrieving call log")
                }
                0 -> {
                    logCursor.close()
                    return@withContext null
                }

                else -> {
                    val dateIndex =
                        logCursor.getColumnIndex(CallLog.Calls.DATE)
                    val callTypeIndex = logCursor.getColumnIndex(CallLog.Calls.TYPE)
                    while (logCursor.moveToNext()) {
                        val callType = logCursor.getInt(callTypeIndex)
                        if (CallLog.Calls.OUTGOING_TYPE == callType || callType == CallLog.Calls.INCOMING_TYPE) {
//                            First valid call
                            val time = logCursor.getLong(dateIndex)
                            logCursor.close()
                            return@withContext Date(time)
                        }
                    }
//                    No call found
                    null
                }

            }
        } catch (e: java.lang.Error) {
            logCursor?.close()
            throw Error("Error retrieving call log")
        }

    }

    @TestOnly
    suspend fun getAllContactsIds(): List<Long> = withContext(dispatcher) {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone._ID)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        try {
            when (cursor?.count) {
                null -> {
                    cursor?.close(); emptyList()

                }
                0 -> {
                    cursor.close(); emptyList()
                }
                else -> {
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
            emptyList()
        }
    }

    suspend fun getContactIdFromUri(uri: Uri): Long? = withContext(dispatcher) {
        val projection = arrayOf(ContactsContract.Contacts.LOOKUP_KEY)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        try {
            when (cursor?.count) {
                null -> {
                    cursor?.close(); null

                }
                0 -> {
                    cursor.close(); null
                }
                else -> {
//                   The lookup key of this contact
                    val lookupKey = cursor.let {
                        val index =
                            cursor.getColumnIndex(
                                ContactsContract.Contacts.LOOKUP_KEY
                            )
                        cursor.moveToNext()
                        val lookupKey = cursor.getString(index)
                        cursor.close()
                        lookupKey
                    }

                    // Get the contact id

                    val commonDataKindsProjection = arrayOf(
                        ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                        ContactsContract.CommonDataKinds.Phone._ID
                    )
                    val selection = "${ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY} = ?"
                    val selectionArgs = arrayOf(lookupKey)
                    val commonDataKindsCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        commonDataKindsProjection,
                        selection,
                        selectionArgs,
                        null
                    )
                    try {
                        when (commonDataKindsCursor?.count) {
                            null -> {
                                commonDataKindsCursor?.close(); null

                            }
                            0 -> {
                                commonDataKindsCursor.close(); null
                            }
                            else -> {
                                val idIndex =
                                    commonDataKindsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
                                commonDataKindsCursor.moveToNext()
                                val id = commonDataKindsCursor.getLong(idIndex)
                                commonDataKindsCursor.close()
                                id
                            }
                        }
                    } catch (e: Error) {
                        commonDataKindsCursor?.close()
                        null
                    }
                }
            }

        } catch (e: Error) {
            cursor?.close()
            null
        }
    }

}