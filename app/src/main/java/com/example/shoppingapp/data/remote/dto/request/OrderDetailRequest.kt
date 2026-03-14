package com.example.shoppingapp.data.remote.dto.request

data class OrderDetailRequest(
    val productDetailId: Int,
    val soLuong: Int,
    val donGia: Long
)
