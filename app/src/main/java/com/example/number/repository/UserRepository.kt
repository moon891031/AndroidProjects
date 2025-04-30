package com.example.number.repository

import com.example.number.api.RetrofitInstance
import com.example.number.model.LoginRequest
import com.example.number.model.LoginTokenRequest
import com.example.number.model.UserInfo
import retrofit2.Response

class UserRepository {

    suspend fun login(loginId: String, password: String,deviceId: String,phoneNumber :String): Response<UserInfo> {
        val loginRequest = LoginRequest(loginId, password, deviceId , phoneNumber)
        return RetrofitInstance.loginService.login(loginRequest)
    }
    suspend fun verifyToken(token: String): Response<UserInfo> {
        val loginTokenRequest = LoginTokenRequest(token)
        return RetrofitInstance.loginTokenService.verifyToken(loginTokenRequest)
    }
}
