package com.example.number

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.transition.TransitionInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.number.repository.UserRepository
import com.example.number.model.UserInfo

class LoginActivity : AppCompatActivity() {

    private val userRepository = UserRepository()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide)
        window.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide)

        setContentView(R.layout.activity_login)


        val usernameEditText: EditText = findViewById(R.id.login_edit_id)
        val passwordEditText: EditText = findViewById(R.id.login_edit_password)
        val loginButton: Button = findViewById(R.id.login_btn_login)
        var isPasswordVisible = false // 초기 상태: 비밀번호 숨김


        //패스워드 on/off
        passwordEditText.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2 // 우측 아이콘의 인덱스
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    // 비밀번호 보이기/숨기기 토글
                    isPasswordVisible = !isPasswordVisible

                    if (isPasswordVisible) {
                        passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_eye_on, 0)
                    } else {
                        passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_eye_off, 0)
                    }

                    // 커서 위치 유지
                    passwordEditText.setSelection(passwordEditText.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }


        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                handleLogin(username, password)
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show()
            }
        }

        val signUpBtn: Button = findViewById(R.id.login_btn_sign_up)
        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.fade_in,   // 화면 진입 애니메이션
                R.anim.fade_out  // 화면 퇴장 애니메이션
            )

            startActivity(intent, options.toBundle())
        }
    }

    private fun handleLogin(username: String, password: String) {
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
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
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