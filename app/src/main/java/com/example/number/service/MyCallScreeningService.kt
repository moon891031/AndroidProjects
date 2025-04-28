package com.example.number.service
import android.telecom.CallScreeningService
import android.telecom.Call
import android.util.Log

class MyCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val number = callDetails.handle?.schemeSpecificPart
        Log.d("MyCallScreening", "전화 왔다: $number")

        respondToCall(callDetails, CallResponse.Builder()
            .setDisallowCall(false) // 전화 차단 안함
            .setRejectCall(false)    // 거부 안함
            .setSkipCallLog(false)   // 통화 기록 남김
            .setSkipNotification(false) // 알림 보여줌
            .build())
    }
}