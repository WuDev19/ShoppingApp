package com.example.shoppingapp.domain.model

data class OrderStatus(
    val orderId: Int,
    val orderStatus: String,
    val orderDate: String,
    val orderExpectedDate: String
)
