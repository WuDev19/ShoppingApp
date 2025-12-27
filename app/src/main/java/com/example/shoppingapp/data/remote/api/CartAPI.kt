package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.request.CartAndCartItemRequest
import com.example.shoppingapp.data.remote.dto.get.ListCartDTO
import com.example.shoppingapp.data.remote.dto.get.ListCartItemDTO
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
    suspend fun getCartUser(@Path("id") id: String, @Query("pageNumber") pageNumber: Int): Response<ListCartDTO>

    @GET("cartItem/get/ofCart/{id}")
    suspend fun getCartItemInCart(@Path("id") id: Int, @Query("pageNumber") pageNumber: Int): Response<ListCartItemDTO>

    @GET("cartItem/get/numberOfCart/{id}")
    suspend fun getNumberOfCartItem(@Path("id") id: String): Response<Int>

}