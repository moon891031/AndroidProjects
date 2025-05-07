package com.example.number


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.number.LoginActivity


class FindIdPasswordActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_find_id_password)
        val btnFindId: Button = findViewById(R.id.findIdPasswordBtnFindId)
        btnFindId.setOnClickListener {
            startActivity(Intent(this@FindIdPasswordActivity, FindIdActivity::class.java))
        }
        val btnFindPassword: Button = findViewById(R.id.findIdPasswordBtnFindPassword)
        btnFindPassword.setOnClickListener {
            startActivity(Intent(this@FindIdPasswordActivity, FindPasswordActivity::class.java))
        }


    }

}