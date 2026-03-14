package com.example.shoppingapp.domain.model

data class CreateOrder (
    var userId: String?,
    var sellerId: Int?,
    val shippingMethod: String,
    val totalAmount: Int,
    val voucherId: Int,
    val createOrderGhn: CreateOrderGhn,
    val orderDetail: List<CreateOrderDetail>
)