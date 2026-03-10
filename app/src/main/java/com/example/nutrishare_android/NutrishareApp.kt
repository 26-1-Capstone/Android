package com.example.nutrishare_android

import android.app.Application
import com.example.nutrishare_android.data.network.RetrofitClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NutrishareApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(applicationContext)
    }
}
