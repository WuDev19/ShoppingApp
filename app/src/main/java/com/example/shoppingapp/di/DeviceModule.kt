package com.example.shoppingapp.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.core.content.edit
import com.example.shoppingapp.constants.SessionManager
import com.example.shoppingapp.constants.TokenManager
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
        val network = connectManager.activeNetwork
            ?: return null //lấy ra các mạng đang hoạt động (wifi, 4g, 5g, ...)
        val linkProperties = connectManager.getLinkProperties(network)
            ?: return null //lấy ra các thông số của danh sách mạng đang kết nối
        for (address in linkProperties.linkAddresses) { //lấy ra các địa chỉ ip
            val ipAddress = address.address //lấy ra kiểu InetAddress để check ipv4 với ipv6
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

    @Provides
    @Singleton
    fun provideTokenManager(sharedPreferences: SharedPreferences): TokenManager {
        return TokenManager(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideSessionManager(): SessionManager {
        return SessionManager()
    }

}