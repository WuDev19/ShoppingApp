package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.PaymentUrlDTO
import com.example.shoppingapp.data.remote.dto.request.OrderPaymentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentAPI {

    @POST("payment/create-payment")
    suspend fun createPayment(@Body orderPaymentRequest: OrderPaymentRequest): Response<PaymentUrlDTO>

}