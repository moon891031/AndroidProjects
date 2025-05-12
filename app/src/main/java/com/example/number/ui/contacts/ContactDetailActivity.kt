package com.example.number.ui.contacts

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.number.R

class ContactDetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_contact_detail)
        val phoneList = listOf("010-1234-5678", "02-9876-5432") // 서버에서 받은 번호 리스트
        val container = findViewById<LinearLayout>(R.id.contactDetailListContainerPhoneNumber)

        phoneList.forEach { number ->
            val textView = TextView(this).apply {
                text = number
                textSize = 16f
                setPadding(0, 8, 0, 8)
            }
            container.addView(textView)
        }
    }

}