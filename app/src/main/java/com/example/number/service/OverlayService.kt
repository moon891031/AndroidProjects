package com.example.number.service

import android.app.Service
import android.content.Context
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.graphics.PixelFormat
import android.content.Intent
import com.example.number.R

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View

    override fun onCreate() {
        super.onCreate()


    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showOverlay()
        return START_NOT_STICKY
    }

    private fun showOverlay() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // 오버레이 레이아웃 인플레이트
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

        // 오버레이를 추가할 때 필요한 LayoutParams 설정
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(overlayView, params)

        // 텍스트 설정
        val callInfoTextView = overlayView.findViewById<TextView>(R.id.callInfoTextView)
        callInfoTextView.text = "전화가 왔습니다!"
        overlayView.setOnClickListener {
            stopSelf()  // 서비스 종료
            windowManager.removeView(overlayView)  // 오버레이 뷰 제거
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (this::windowManager.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}