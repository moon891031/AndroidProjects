package com.example.number.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.number.R

class ForegroundService : Service() {

    private val channelId = "phone_service_channel"
    private var isForegroundRunning = false // 포그라운드 실행 여부

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Phone Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isForegroundRunning) {
            Log.d("moon", "포어그라운드 서비스가 이미 실행 중입니다.")
        } else {
            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("전화 서비스")
                .setContentText("앱이 포그라운드 상태입니다.")
                .setSmallIcon(R.drawable.ic_boda_logo) // 아이콘 설정
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            startForeground(1, notification)
            isForegroundRunning = true
            Log.d("moon", "startForeground/PhoneService 시작")
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isForegroundRunning = false
        Log.d("moon", "ForegroundService 종료")
    }
}
