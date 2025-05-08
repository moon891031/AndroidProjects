package com.example.number

import android.app.Application
import com.example.number.api.RetrofitInstance

class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        RetrofitInstance.init(this) // Retrofit 초기화
    }
}