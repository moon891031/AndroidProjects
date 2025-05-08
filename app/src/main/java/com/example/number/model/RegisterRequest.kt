package com.example.number.model

data class RegisterRequest(
    val loginId: String,
    val password: String,
    val fullName: String,
    val birthDate: Int,
    val gender: String,
    val deviceInfo: DeviceInfo
)

data class DeviceInfo(
    val deviceId: String,
    val osType: String = "ANDROID",
    val phoneNumber: String
)