package com.example.number

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)
        val passwordEditText = findViewById<EditText>(R.id.sign_up_edit_password)
        val confirmPasswordEditText = findViewById<EditText>(R.id.sign_up_edit_password_confirm)
        val passwordConfirmTextView = findViewById<TextView>(R.id.sign_up_tv_password_confirm)

        // EditText에 TextWatcher 추가
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = passwordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    if (password == confirmPassword) {
                        passwordConfirmTextView.text = "비밀번호가 일치합니다."
                        passwordConfirmTextView.setTextColor(getColor(R.color.teal_200)) // 성공 색상
                    } else {
                        passwordConfirmTextView.text = "비밀번호가 일치하지 않습니다."
                        passwordConfirmTextView.setTextColor(getColor(R.color.purple_500)) // 실패 색상
                    }
                } else {
                    passwordConfirmTextView.text = "" // 초기 상태
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        passwordEditText.addTextChangedListener(textWatcher)
        confirmPasswordEditText.addTextChangedListener(textWatcher)
    }
}