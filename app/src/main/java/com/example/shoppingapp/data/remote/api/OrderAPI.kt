package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.OrderDTO
import com.example.shoppingapp.data.remote.dto.get.OrderDetailDTO
import com.example.shoppingapp.data.remote.dto.request.OrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderAPI {

    @POST("order/create-order/{shopGhnId}")
    suspend fun createOrder(
        @Path("shopGhnId") shopGhnId: Int,
        @Body orderRequest: OrderRequest
    ): Response<OrderDTO>

    @PATCH("order/update-order/{orderId}")
    suspend fun updateStatusOrder(@Path("orderId") orderId: String): Response<OrderDTO>

    @GET("order/details/{orderId}")
    suspend fun getOrderDetail(@Path("orderId") orderId: String): Response<OrderDetailDTO>

}