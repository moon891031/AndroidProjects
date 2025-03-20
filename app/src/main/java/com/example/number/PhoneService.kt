package com.example.number

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class PhoneService : Service() {

    private val channelId = "phone_service_channel"

    override fun onCreate() {
        super.onCreate()

        // 알림 채널 설정 (안드로이드 8.0 이상에서는 필수)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Phone Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // 포그라운드 서비스 시작
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("전화 서비스")
            .setContentText("앱이 포그라운드 상태입니다.")
            .setSmallIcon(R.drawable.ic_notification) // 아이콘 설정
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // 앱을 포그라운드 상태로 전환
        startForeground(1, notification)
        Log.d("moon", "startForeground/PhoneService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 전화가 오면 처리할 코드 (예: 번호 비교 후 알림 등)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
