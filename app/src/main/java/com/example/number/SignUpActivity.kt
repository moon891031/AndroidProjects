package com.example.number

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.number.model.DeviceInfo
import com.example.number.model.RegisterRequest
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.number.repository.UserRepository

class SignUpActivity : AppCompatActivity() {
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var passwordConfirmTextView: TextView
    private lateinit var idEditText: EditText
    private val userRepository = UserRepository()
    private var isLoginIdChecked = false
    private var lastCheckedLoginId: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPhonePermission()
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)
        passwordEditText = findViewById(R.id.signUpEditPassword)
        confirmPasswordEditText = findViewById(R.id.signUpEditPasswordConfirm)
        passwordConfirmTextView = findViewById(R.id.signUpTvPasswordConfirm)
        idEditText = findViewById(R.id.signUpEditId)
        var nameEditText: EditText = findViewById(R.id.signUpEditName)
        var dateEditText: EditText = findViewById(R.id.signUpEditDate)
        var genderEditText: EditText = findViewById(R.id.signUpEditGender)
        val signUpBtn: Button = findViewById(R.id.signUpBtnSignUp)
        val loginIdCheckBtn : Button = findViewById(R.id.signUpBtnIdConfirm)
        var isPasswordVisible = false // 초기 상태: 비밀번호 숨김
        loginIdCheckBtn.setOnClickListener {
            onCheckLoginIdClicked()
        }
        signUpBtn.setOnClickListener {
            var id = idEditText.text.toString()
            var password = passwordEditText.text.toString()
            var confirmPassword = confirmPasswordEditText.text.toString()
            var name = nameEditText.text.toString()
            var dateString = dateEditText.text.toString()
            var gender = genderEditText.text.toString()

            // 필수 입력란이 비어 있는지 체크
            if (name.isEmpty()) {
                Toast.makeText(applicationContext, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (id.isEmpty()) {
                Toast.makeText(applicationContext, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(applicationContext, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                Toast.makeText(applicationContext, "비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dateString.isEmpty()) {
                Toast.makeText(applicationContext, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (gender.isEmpty()) {
                Toast.makeText(applicationContext, "성별을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 비밀번호와 비밀번호 확인이 일치하는지 확인
            if (password != confirmPassword) {
                Toast.makeText(applicationContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 생년월일을 Int로 변환
            var birthDate = dateString.toIntOrNull()
            if (birthDate == null) {
                Toast.makeText(applicationContext, "유효한 생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isLoginIdChecked || lastCheckedLoginId != id) {
                Toast.makeText(applicationContext, "아이디 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            handleRegister(id, password, name, birthDate, gender)
        }
        idEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isLoginIdChecked = false
                lastCheckedLoginId = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        confirmPasswordEditText.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2 // 우측 아이콘의 인덱스
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (confirmPasswordEditText.right - confirmPasswordEditText.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    // 비밀번호 보이기/숨기기 토글
                    isPasswordVisible = !isPasswordVisible

                    if (isPasswordVisible) {
                        confirmPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_on2, 0)
                    } else {
                        confirmPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                        confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off2, 0)
                    }

                    // 커서 위치 유지
                    confirmPasswordEditText.setSelection(confirmPasswordEditText.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePasswords()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        confirmPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePasswords()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
    fun onCheckLoginIdClicked() {
        val loginId = idEditText.text.toString()

        if (loginId.isNotEmpty()) {
            checkLoginIdAvailability(loginId)
        } else {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun checkLoginIdAvailability(loginId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = userRepository.checkLoginIdAvailability(loginId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isAvailable) {
                            Toast.makeText(applicationContext, "아이디 사용 가능합니다.", Toast.LENGTH_SHORT).show()
                            isLoginIdChecked = true
                            lastCheckedLoginId = loginId
                        } else {
                            Toast.makeText(applicationContext, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show()
                            isLoginIdChecked = false
                            lastCheckedLoginId = null
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "아이디 확인 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun validatePasswords() {
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val confirmPasswordInputLayout = findViewById<TextInputLayout>(R.id.signUpInputLayoutPasswordConfirm)

        if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                passwordConfirmTextView.text = "비밀번호가 일치합니다."
                passwordConfirmTextView.setTextColor(ContextCompat.getColor(this, R.color.blue))
                confirmPasswordInputLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.blue)
            } else {
                passwordConfirmTextView.text = "비밀번호가 일치하지 않습니다."
                passwordConfirmTextView.setTextColor(ContextCompat.getColor(this, R.color.red))
                confirmPasswordInputLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
            }
        } else {
            passwordConfirmTextView.text = ""
        }
    }
    private fun handleRegister(loginId: String, password: String, fullName: String, birthDate: Int, gender: String) {
        val deviceId = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //val phoneNumber = telephonyManager.line1Number ?: ""
        val phoneNumber="01068657633"
        val registerRequest = RegisterRequest(
            loginId = loginId,
            password = password,
            fullName = fullName,
            birthDate = birthDate,
            gender = gender,
            deviceInfo = DeviceInfo(
                deviceId = deviceId,
                phoneNumber = phoneNumber
            )
        )

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = userRepository.register(registerRequest)
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(applicationContext, "회원가입 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.d("BODA_register", "deviceID:$deviceId/phoneNumber:$phoneNumber")
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
    private fun checkPhonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE),
                1001
            )
        }
    }
}