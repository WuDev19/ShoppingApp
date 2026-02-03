package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.request.GenerateTokenRequest
import com.example.shoppingapp.data.remote.dto.request.LogoutRequest
import com.example.shoppingapp.data.remote.dto.request.RefreshRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthAPI {

    @POST("auth/token")
    suspend fun token(@Body generateTokenRequest: GenerateTokenRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Body logoutRequest: LogoutRequest): Response<Unit>

    @POST("auth/refresh")
    suspend fun refresh(@Body refreshRequest: RefreshRequest): Response<AuthResponse>

}