package com.example.number

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Login 화면에 표시할 레이아웃 설정
        setContentView(R.layout.activity_login)

        // 로그인 버튼 클릭 시 MainActivity로 이동
        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            // 로그인 성공 처리 후 MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // LoginActivity 종료
        }
    }
}
