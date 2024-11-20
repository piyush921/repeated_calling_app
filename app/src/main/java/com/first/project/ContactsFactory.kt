package com.first.project

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.first.project.model.Contact

class ContactsFactory(private val context: Context) {

    fun getContacts(): List<Contact> {
        val contactsList: ArrayList<Contact> = ArrayList()

        val contentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {

                val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val hasPhoneIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

                if (idIndex >= 0 && nameIndex >= 0 && hasPhoneIndex >= 0) {
                    val id = cursor.getString(idIndex)
                    val name = cursor.getString(nameIndex)
                    if (cursor.getInt(hasPhoneIndex) > 0) {
                        val phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )

                        if (phoneCursor != null) {
                            while (phoneCursor.moveToNext()) {
                                val phoneIndex =
                                    phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                if (phoneIndex >= 0) {
                                    val phoneNumber = phoneCursor.getString(phoneIndex)
                                    contactsList.add(Contact(id, name, phoneNumber))
                                }
                            }
                            phoneCursor.close()
                        }
                    }
                }
            }
        }
        cursor?.close()

        return contactsList
    }

}