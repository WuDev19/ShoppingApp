package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.CartAndCartItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CartAPI {

    @POST("cart/create")
    suspend fun createCart(@Body cartAndCartItemRequest: CartAndCartItemRequest): Response<Any>

}