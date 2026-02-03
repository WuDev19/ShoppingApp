package com.example.shoppingapp.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.core.content.edit
import com.example.shoppingapp.di.qualifier.DeviceId
import com.example.shoppingapp.di.qualifier.IpAddress
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.net.Inet4Address
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeviceModule {
    private const val DEVICE_KEY = "device_id"

    @IpAddress
    @Provides
    @Singleton
    fun provideIpAddress(@ApplicationContext context: Context): String? {
        val connectManager =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager // sử dụng dịch vụ hệ thống connectivity manager, cách này mới ko bị deprecate
        val network = connectManager.activeNetwork ?: return null
        val linkProperties = connectManager.getLinkProperties(network) ?: return null
        for (address in linkProperties.linkAddresses) {
            val ipAddress = address.address
            if (ipAddress is Inet4Address && !ipAddress.isLoopbackAddress) {
                return ipAddress.hostAddress
            }
        }
        return null
    }

    @DeviceId
    @Provides
    @Singleton
    fun provideDeviceId(sharedPreferences: SharedPreferences): String {
        var id = sharedPreferences.getString(DEVICE_KEY, null)
        if (id == null) {
            id = UUID.randomUUID().toString()
            sharedPreferences.edit { putString(DEVICE_KEY, id) }
        }
        return id
    }

}