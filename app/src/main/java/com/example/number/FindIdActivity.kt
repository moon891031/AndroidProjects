package com.example.number


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity



class FindIdActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_find_id)

    }

}