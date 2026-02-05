package com.example.shoppingapp.data.remote.dto.request

data class RefreshRequest(val token: String, val refreshToken: String, val ipAddress: String?)
