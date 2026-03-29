package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.CancelOrderDTO
import com.example.shoppingapp.data.remote.dto.get.OrderDTO
import com.example.shoppingapp.data.remote.dto.get.OrderDetailDTO
import com.example.shoppingapp.data.remote.dto.get.PageResponse
import com.example.shoppingapp.data.remote.dto.request.CancelOrderRequest
import com.example.shoppingapp.data.remote.dto.request.OrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("order/via-status/{userId}")
    suspend fun getOrderViaStatus(
        @Path("userId") userId: String,
        @Query("status") status: String,
        @Query("page") page: Int
    ): Response<PageResponse<OrderDetailDTO>>

    @POST("order/cancel/{orderId}")
    suspend fun cancelOrder(
        @Path("orderId") orderId: String,
        @Body cancelOrderRequest: CancelOrderRequest
    ): Response<CancelOrderDTO>

}