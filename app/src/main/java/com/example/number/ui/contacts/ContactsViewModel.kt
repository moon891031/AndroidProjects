package com.example.number.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.number.model.Contact
import com.example.number.model.ContactResponse
import com.example.number.repository.ContactRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class ContactsViewModel : ViewModel() {
    private val contactRepository = ContactRepository()

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> get() = _contacts

    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> get() = _totalCount

    fun fetchContacts(page: Int, pageSize: Int, userId: Int, contactCategoryId: Int? = null, searchText: String? = null) {
        viewModelScope.launch {
            val response: Response<ContactResponse> = contactRepository.getContacts(page, pageSize, userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    _contacts.value = it.contactList
                    _totalCount.value = it.totalCount
                }
            }
        }
    }
}
