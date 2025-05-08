package com.example.number.auth

import android.content.Context
import com.example.number.utils.AuthManager

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // 토큰이 필요 없는 API 예외 처리
        val excludedPaths = listOf("/auth/login", "/auth/register")
        val requestPath = request.url.encodedPath

        return if (excludedPaths.any { requestPath.startsWith(it) }) {
            chain.proceed(request) // 토큰 없이 진행
        } else {
            val token = AuthManager.getAccessToken()

            val newRequest = if (!token.isNullOrEmpty()) {
                request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                request
            }

            chain.proceed(newRequest)
        }
    }
}