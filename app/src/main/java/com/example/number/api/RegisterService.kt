package com.example.number.api

import com.example.number.model.LoginIdAvailabilityResponse
import com.example.number.model.RegisterRequest
import com.example.number.model.UserInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RegisterService {
    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit> // 성공 시 200 또는 201 응답

    @GET("/user/{loginId}/validate-login-id")
    suspend fun checkLoginIdAvailability(@Path("loginId") loginId: String): Response<LoginIdAvailabilityResponse> // 아이디 중복 확인
}
