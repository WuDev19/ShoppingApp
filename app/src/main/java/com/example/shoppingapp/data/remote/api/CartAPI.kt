package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.request.CartAndCartItemRequest
import com.example.shoppingapp.data.remote.dto.get.ListCartDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartAPI {

    @POST("cart/create")
    suspend fun createCart(@Body cartAndCartItemRequest: CartAndCartItemRequest): Response<Any>

    @GET("cart/get/{userId}")
    suspend fun getCartUser(
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int
    ): Response<ListCartDTO>

    @GET("cartItem/get/numberOfCart/{id}")
    suspend fun getNumberOfCartItem(@Path("id") id: String): Response<Int>

}