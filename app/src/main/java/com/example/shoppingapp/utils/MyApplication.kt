package com.example.shoppingapp.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val config = mapOf(
            StringConstants.CLOUD_NAME to StringConstants.CLOUDINARY_CLOUD_NAME,
            StringConstants.SECURE to true
        )
        MediaManager.init(this, config)
        createChanelId()
    }

    private fun createChanelId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                StringConstants.SEND_OTP_CHANEL_ID,
                StringConstants.DESCRIPTION_SEND_OTP_CHANEL,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.let {
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }

}