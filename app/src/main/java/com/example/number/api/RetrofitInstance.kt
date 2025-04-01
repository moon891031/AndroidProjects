package com.example.number.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://54.180.98.14:3000" // 서버 URL

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val contactService: ContactService by lazy {
        retrofit.create(ContactService::class.java)
    }
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}