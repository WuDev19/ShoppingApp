package com.example.shoppingapp.data.remote.dto.request

data class GenerateTokenRequest(val userId: String, val deviceId: String?, val ipAddress: String?)
