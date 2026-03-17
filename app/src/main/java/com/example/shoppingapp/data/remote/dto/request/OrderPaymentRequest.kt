package com.example.shoppingapp.data.remote.dto.request

data class OrderPaymentRequest(
    val orderId: String,
    val amount: Long
)
