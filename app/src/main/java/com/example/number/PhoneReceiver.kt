package com.example.number

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.number.service.OverlayService

class PhoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    Log.d("PhoneReceiver", "전화가 왔습니다!")
                    showNotification(context, "전화가 왔습니다!")

                    // 📌 전화가 왔을 때 OverlayService 실행
                    val overlayIntent = Intent(context, OverlayService::class.java)
                    context.startService(overlayIntent)
                }

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    Log.d("PhoneReceiver", "통화 중입니다.")
                    showNotification(context, "통화 중입니다.")
                }

                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Log.d("PhoneReceiver", "전화가 종료되었습니다.")
                    showNotification(context, "전화가 종료되었습니다.")

                    // 📌 전화가 종료되면 Overlay 제거 (서비스 종료)
                    val stopOverlayIntent = Intent(context, OverlayService::class.java)
                    context.stopService(stopOverlayIntent)
                }
            }
        }
    }

    private fun showNotification(context: Context, message: String) {
        val channelId = "phone_call_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "전화 알림",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("전화 상태 변경")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
