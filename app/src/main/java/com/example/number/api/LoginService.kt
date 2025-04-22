package com.example.number.api

import com.example.number.model.LoginRequest
import com.example.number.model.UserInfo

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<UserInfo>
}

