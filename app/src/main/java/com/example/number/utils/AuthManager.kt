package com.example.number.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.number.MyApplication

object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ACCESS = "accessToken"
    private const val KEY_REFRESH = "refreshToken"

    private val prefs: SharedPreferences by lazy {
        MyApplication.instance.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // AccessToken 저장
    fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS, token).apply()
    }

    // RefreshToken 저장
    fun saveRefreshToken(token: String) {
        prefs.edit().putString(KEY_REFRESH, token).apply()
    }

    // AccessToken 가져오기
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS, null)
    }

    // RefreshToken 가져오기
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH, null)
    }

    // 토큰 삭제
    fun clearTokens() {
        prefs.edit().remove(KEY_ACCESS).remove(KEY_REFRESH).apply()
    }
}