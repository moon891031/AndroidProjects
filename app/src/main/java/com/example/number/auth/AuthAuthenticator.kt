package com.example.number.auth

import android.content.Context
import android.util.Log
import com.example.number.auth.TokenRefreshService
import com.example.number.utils.AuthManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
private const val BASE_URL = "http://54.180.98.14:3000" // 서버 URL
class AuthAuthenticator(private val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = AuthManager.getRefreshToken() ?: return null

        try {
            // 토큰 재발급 요청
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(TokenRefreshService::class.java)
            val res = service.refreshToken(mapOf("refreshToken" to refreshToken)).execute()

            if (res.isSuccessful) {
                val newAccessToken = res.body()?.accessToken ?: return null
                val newRefreshToken = res.body()?.refreshToken ?: return null

                // 새로운 토큰 저장
                AuthManager.saveAccessToken(newAccessToken)
                AuthManager.saveRefreshToken(newRefreshToken)

                // 새로운 토큰으로 요청 다시 시도
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            }
        } catch (e: Exception) {
            Log.e("AuthAuthenticator", "토큰 재발급 실패", e)
        }

        return null // 실패하면 로그인 페이지 유도
    }
}