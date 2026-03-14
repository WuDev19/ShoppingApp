package com.example.shoppingapp.domain.model

data class CreatePayment(
    val description: String,
    val currency: String,
    val paymentMethod: String,
    val additionalData: Map<String, String>?
)
