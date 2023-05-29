package org.bmsk.lifemash_newsapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class LifeMashApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}