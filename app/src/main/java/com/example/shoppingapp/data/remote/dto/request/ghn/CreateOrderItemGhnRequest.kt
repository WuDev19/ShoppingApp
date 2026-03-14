package com.example.shoppingapp.data.remote.dto.request.ghn

data class CreateOrderItemGhnRequest(
    val name: String,
    val quantity: Int,
    val price: Long,
    val weight: Int,
    val length: Int,
    val width: Int,
    val height: Int
)