package com.example.yecwms.core

import android.util.Log
import com.example.yecwms.data.Preferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val sessionId = Preferences.sessionID
        val companyDB = Preferences.companyDB

        Log.d("USER_SERVICE_AUTH", sessionId.toString())

        val request: Request = if (sessionId != null && companyDB != null) {
            chain.request()
                .newBuilder()
                .addHeader("Cookie", "B1SESSION=$sessionId")
                .addHeader("Cookie", "CompanyDB=$companyDB")
                .build()
        } else chain.request()

        return chain.proceed(request)


    }
}