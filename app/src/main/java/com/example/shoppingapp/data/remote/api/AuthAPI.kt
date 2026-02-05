package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.get.UserDTOGet
import com.example.shoppingapp.data.remote.dto.request.GenerateTokenRequest
import com.example.shoppingapp.data.remote.dto.request.LogoutRequest
import com.example.shoppingapp.data.remote.dto.request.RefreshRequest
import com.example.shoppingapp.data.remote.dto.request.UserDTOCreate
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthAPI {

    @POST("auth/create")
    suspend fun createUser(@Body user: UserDTOCreate): Response<UserDTOGet>

    @POST("auth/token")
    suspend fun token(@Body generateTokenRequest: GenerateTokenRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Body logoutRequest: LogoutRequest): Response<Unit>

    @POST("auth/refresh")
    fun refresh(@Body refreshRequest: RefreshRequest): Call<AuthResponse> //ko dùng suspend vì okhttp ko hỗ trợ coroutine và nó chạy đồng bộ tại tầng mạng của okhttp java

}