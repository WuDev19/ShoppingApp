package com.example.shoppingapp.data.remote.dto.request

data class OrderPaymentRequest(
    val orderId: Int,
    val amount: Long
)
