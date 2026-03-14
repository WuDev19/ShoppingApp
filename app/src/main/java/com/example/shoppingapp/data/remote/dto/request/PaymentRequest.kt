package com.example.shoppingapp.data.remote.dto.request

data class PaymentRequest(
    val description: String,
    val currency: String,
    val paymentMethod: String,
    val additionalData: Map<String, String>?
)
