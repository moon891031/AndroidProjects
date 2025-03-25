package com.example.number

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


import com.example.number.api.RetrofitInstance
import com.example.number.model.LoginRequest
import com.example.number.repository.UserRepository
import com.example.number.model.UserInfo

class LoginActivity : AppCompatActivity() {

    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText: EditText = findViewById(R.id.username)
        val passwordEditText: EditText = findViewById(R.id.password)

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                handleLogin(username, password)
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        val signUpLink: TextView = findViewById(R.id.signUpLink)
        signUpLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleLogin(username: String, password: String) {
      //  val loginRequest = LoginRequest(username, password)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = userRepository.login(username, password)
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()

                        saveUserInfo(result)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(applicationContext, "로그인 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun saveUserInfo(userInfo: UserInfo) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userInfo.userId)
        editor.putString("username", userInfo.username)
        editor.putString("token", userInfo.token)
        editor.apply()
    }
}