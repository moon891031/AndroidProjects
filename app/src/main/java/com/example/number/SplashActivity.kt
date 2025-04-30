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

        // 일정 시간 후 LoginActivity로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
            finish()  // SplashActivity 종료
        }, 3000) // 2초 후 LoginActivity로 이동
    }
}
