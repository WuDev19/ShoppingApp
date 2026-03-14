package com.example.shoppingapp.data.remote.dto.request

import com.example.shoppingapp.data.remote.dto.request.ghn.CreateOrderGhnRequest

data class OrderRequest(
    val userId: String,
    val sellerId: Int,
    val shippingMethod: String,
    val paymentMethod: String,
    val totalAmount: Long,
    val voucherId: Int,
    val note: String,
    val createOrderGhnRequest: CreateOrderGhnRequest,
    val orderDetailRequests: List<OrderDetailRequest>
)
