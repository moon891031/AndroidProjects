package com.example.number.auth
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenRefreshService {
    @POST("/auth/access-token")
    fun refreshToken(@Body body: Map<String, String>): Call<TokenResponse>
}

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)