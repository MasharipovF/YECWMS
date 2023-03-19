package com.example.yecwms.presentation

import android.app.Application
import com.example.yecwms.data.Preferences

class SapMobileApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Preferences.init(this)
    }



}