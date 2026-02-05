package com.example.shoppingapp.constants

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.shoppingapp.data.remote.dto.get.AuthResponse

class TokenManager(private val sharedPreferences: SharedPreferences) {

    fun getAccessToken(): String? {
        return sharedPreferences.getString(StringConstants.TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(StringConstants.REFRESH_TOKEN, null)
    }

    fun writeAccessToken(token: String?) {
        sharedPreferences.edit {
            putString(StringConstants.TOKEN, token)
        }
    }

    fun writeRefreshToken(refreshToken: String?) {
        sharedPreferences.edit {
            putString(StringConstants.REFRESH_TOKEN, refreshToken)
        }
    }

    fun writeCreatedTokenTime(time: String?) {
        sharedPreferences.edit {
            putString(StringConstants.TIME_CREATE_TOKEN, time)
        }
    }

    fun writeAll(authResponse: AuthResponse) {
        writeAccessToken(authResponse.token)
        writeRefreshToken(authResponse.refreshToken)
        writeCreatedTokenTime(authResponse.createTime)
    }

    fun deleteAll() {
        sharedPreferences.edit {
            remove(StringConstants.TOKEN)
            remove(StringConstants.REFRESH_TOKEN)
            remove(StringConstants.TIME_CREATE_TOKEN)
        }
    }

}