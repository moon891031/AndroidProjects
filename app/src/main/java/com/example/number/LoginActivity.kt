package com.example.number

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.transition.TransitionInflater
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.number.repository.UserRepository
import com.example.number.model.UserInfo

class LoginActivity : AppCompatActivity() {

    private val userRepository = UserRepository()
    private val PERMISSION_REQUEST_CODE = 100
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide)
        window.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide)
        setContentView(R.layout.activity_login)
        usernameEditText = findViewById(R.id.loginEditId)
        passwordEditText = findViewById(R.id.loginEditPassword)
        val loginButton: Button = findViewById(R.id.loginBtnLogin)
        var isPasswordVisible = false
        passwordEditText.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == android.view.MotionEvent.ACTION_UP &&
                event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[DRAWABLE_END].bounds.width())
            ) {
                isPasswordVisible = !isPasswordVisible
                passwordEditText.transformationMethod =
                    if (isPasswordVisible) HideReturnsTransformationMethod.getInstance()
                    else PasswordTransformationMethod.getInstance()

                val icon = if (isPasswordVisible) R.drawable.login_eye_on else R.drawable.login_eye_off
                passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)
                passwordEditText.setSelection(passwordEditText.text.length)
                return@setOnTouchListener true
            }
            false
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                checkPermissionsAndLogin(username, password)
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.loginBtnSignUp).setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            startActivity(intent, options.toBundle())
        }

        findViewById<TextView>(R.id.loginBtnFindIdPassword).setOnClickListener {
            val intent = Intent(this, FindIdPasswordActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            startActivity(intent, options.toBundle())
        }
    }

    private fun checkPermissionsAndLogin(username: String, password: String) {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE
        )

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            handleLogin(username, password)
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleLogin(username: String, password: String) {
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //val phoneNumber = telephonyManager.line1Number ?: "" // 일부 기기에서는 null일 수 있음
        val phoneNumber = "01068657633"
        lifecycleScope.launch {
            try {
                val response = userRepository.login(username, password, deviceId, phoneNumber)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                        saveUserInfo(it)
                        Log.d("BODA_login", "deviceID:$deviceId/phoneNumber:$phoneNumber")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(applicationContext, "로그인 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()
                handleLogin(username, password)
            } else {
                Toast.makeText(this, "필수 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserInfo(userInfo: UserInfo) {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        with(prefs.edit()) {
            putString("accessToken", userInfo.accessToken)
            putString("refreshToken", userInfo.refreshToken)
            Log.d("BODA_login_saveUserInfo", userInfo.accessToken +"/"+ userInfo.refreshToken)
            apply()
        }
    }

}