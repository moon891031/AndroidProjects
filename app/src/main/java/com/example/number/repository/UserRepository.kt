package com.example.number.repository

import com.example.number.api.RetrofitInstance
import com.example.number.model.LoginRequest
import com.example.number.model.UserInfo
import retrofit2.Response

class UserRepository {

    suspend fun login(username: String, password: String): Response<UserInfo> {
        val loginRequest = LoginRequest(username, password)
        return RetrofitInstance.api.login(loginRequest)
    }
}