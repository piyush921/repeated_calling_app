package com.first.project

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.first.project.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class MainViewModel(private val contactsFactory: ContactsFactory) : ViewModel() {

    val themeState: MutableState<Boolean> = mutableStateOf(false)
    val contact: MutableState<Contact> = mutableStateOf(Contact("", "", ""))
    val contactsProgress: MutableState<Boolean> = mutableStateOf(true)

    val contactsFlow: Flow<List<Contact>> = contactsFactory.getContacts()
        .catch { exception ->
            Log.d("error", "${exception.message}")
        }

    fun validatePhone(): Boolean {
        return contact.value.id?.isEmpty() != true
    }

}

class ContactsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(ContactsFactory(context.applicationContext)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}