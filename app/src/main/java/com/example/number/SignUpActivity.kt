package com.example.number

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpActivity : AppCompatActivity() {
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var passwordConfirmTextView: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)
        passwordEditText = findViewById(R.id.signUpEditPassword)
        confirmPasswordEditText = findViewById(R.id.signUpEditPasswordConfirm)
        passwordConfirmTextView = findViewById(R.id.signUpTvPasswordConfirm)

        var isPasswordVisible = false // 초기 상태: 비밀번호 숨김


        confirmPasswordEditText.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2 // 우측 아이콘의 인덱스
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (confirmPasswordEditText.right - confirmPasswordEditText.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    // 비밀번호 보이기/숨기기 토글
                    isPasswordVisible = !isPasswordVisible

                    if (isPasswordVisible) {
                        confirmPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_on, 0)
                    } else {
                        confirmPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                        confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0)
                    }

                    // 커서 위치 유지
                    confirmPasswordEditText.setSelection(confirmPasswordEditText.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }


    /*
        // EditText에 TextWatcher 추가
        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePasswords()
            }
        }

        confirmPasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePasswords()
            }
        }
 */
        val passwordEditText = findViewById<TextInputEditText>(R.id.signUpEditPassword)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.signUpEditPasswordConfirm)

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


}