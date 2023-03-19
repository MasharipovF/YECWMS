package com.example.yecwms.core

import android.util.Log
import com.example.yecwms.core.UnsafeOkHttpClient.UnsafeOkHttpClient
import com.example.yecwms.core.UnsafeOkHttpClient.UnsafeOkHttpClientWithInterceptor
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.remote.services.LoginService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {


    inline fun <reified S> createService(): S {
        return getRetrofitInstance().create(S::class.java)
    }

    fun createLoginService(): LoginService {
        Log.d("USER_SERVICE", "LOGIN")
        return getRetrofitInstanceForLogin().create(LoginService::class.java)
    }

    fun <T> createService(serviceType: Class<T>): T {
        return getRetrofitInstance().create(serviceType)
    }

    private fun getRetrofitInstanceForLogin(): Retrofit {
        val BASE_URL: String = "https://${Preferences.ipAddress.toString()}:${Preferences.portNumber.toString()}/b1s/v2/"
        Log.wtf("BASEURL", BASE_URL.toString())
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(UnsafeOkHttpClient)
            .build()
    }

    fun getRetrofitInstance(): Retrofit {
        val BASE_URL: String = "https://${Preferences.ipAddress.toString()}:${Preferences.portNumber.toString()}/b1s/v2/"

        Log.d("USER_SERVICE", "INSIDE RETROFIT")
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(UnsafeOkHttpClientWithInterceptor)
            .build()
    }
}

