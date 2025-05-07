package com.example.number.repository

import com.example.number.api.RetrofitInstance
import com.example.number.model.LoginRequest
import com.example.number.model.UserInfo
import retrofit2.Response

class UserRepository {

    suspend fun login(loginId: String, password: String,deviceId: String,phoneNumber :String): Response<UserInfo> {
        val loginRequest = LoginRequest(loginId, password, deviceId , phoneNumber)
        return RetrofitInstance.loginService.login(loginRequest)
    }

}
