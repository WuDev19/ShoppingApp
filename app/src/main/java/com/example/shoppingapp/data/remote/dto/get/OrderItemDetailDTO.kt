package com.example.shoppingapp.data.remote.dto.get

data class OrderItemDetailDTO (
    val orderItemId: Int,
    val detailProduct: ProductChiTietDTO,
    val number: Int,
    val price: Int
)