package com.example.number.repository

import com.example.number.api.RetrofitInstance
import com.example.number.model.ContactResponse
import retrofit2.Response

class ContactRepository {

    suspend fun getContacts(userId: Int, page: Int, pageSize: Int,searchText: String?=null): Response<ContactResponse> {
        return RetrofitInstance.contactService.getContacts(userId, page, pageSize, searchText ?: "")
    }
}