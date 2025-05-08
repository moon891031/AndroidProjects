package com.example.number

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.os.Looper
import android.transition.TransitionInflater


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Splash 화면에 표시할 레이아웃 설정
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val accessToken = prefs.getString("accessToken", null)
        // 일정 시간 후 LoginActivity로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            //val intent = Intent(this, LoginActivity::class.java)

            if (!accessToken.isNullOrEmpty()) {
                // 토큰이 있으니 자동 로그인
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // 토큰이 없으면 로그인 화면으로
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            finish()  // SplashActivity 종료
        }, 1000) // 2초 후 LoginActivity로 이동
    }
}
