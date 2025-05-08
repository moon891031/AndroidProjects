package com.example.number.repository

import com.example.number.api.RetrofitInstance
import com.example.number.model.LoginIdAvailabilityResponse
import com.example.number.model.LoginRequest
import com.example.number.model.RegisterRequest
import com.example.number.model.UserInfo
import retrofit2.Response

class UserRepository {

    suspend fun login(loginId: String, password: String,deviceId: String,phoneNumber :String): Response<UserInfo> {
        val loginRequest = LoginRequest(loginId, password, deviceId , phoneNumber)
        return RetrofitInstance.loginService.login(loginRequest)
    }
    suspend fun register(request: RegisterRequest): Response<Unit> {
        return RetrofitInstance.registerService.register(request)
    }
    suspend fun checkLoginIdAvailability(loginId: String): Response<LoginIdAvailabilityResponse> {
        return RetrofitInstance.registerService.checkLoginIdAvailability(loginId)
    }

}
