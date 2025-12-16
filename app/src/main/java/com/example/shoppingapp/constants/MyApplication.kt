package com.example.shoppingapp.constants

import android.app.Application
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
    }

}