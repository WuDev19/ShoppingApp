package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.CartAndCartItemRequest
import com.example.shoppingapp.data.remote.dto.ListCartDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartAPI {

    @POST("cart/create")
    suspend fun createCart(@Body cartAndCartItemRequest: CartAndCartItemRequest): Response<Any>

    @GET("cart/get/{id}")
    suspend fun getCartUser(@Path("id") id: Int, @Query("pageNumber") pageNumber: Int): Response<ListCartDTO>

}