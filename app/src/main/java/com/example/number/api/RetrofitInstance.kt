package com.example.number.api


import android.content.Context
import com.example.number.auth.AuthAuthenticator
import com.example.number.auth.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://54.180.98.14:3000" // 서버 URL

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .authenticator(AuthAuthenticator(context))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val contactService: ContactService by lazy {
        retrofit.create(ContactService::class.java)
    }

    val loginService: LoginService by lazy {
        retrofit.create(LoginService::class.java)
    }

    val registerService: RegisterService by lazy {
        retrofit.create(RegisterService::class.java)
    }
    val contactGroupService: ContactGroupService by lazy {
        retrofit.create(ContactGroupService::class.java)
    }
}