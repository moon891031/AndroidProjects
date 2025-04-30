package com.example.number.model

data class LoginRequest(
    val loginId: String,
    val password: String,
    val deviceId : String,
    val phoneNumber : String,
)