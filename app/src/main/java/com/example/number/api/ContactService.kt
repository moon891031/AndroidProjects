package com.example.number.api

import com.example.number.model.ContactResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ContactService {

    @GET("/contact/list")
    suspend fun getContacts(
        @Query("userId") userId: Int,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("searchText") query: String? = null
    ): Response<ContactResponse> // ContactResponse는 반환 받을 데이터의 구조
}
