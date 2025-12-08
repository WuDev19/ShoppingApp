package com.example.shoppingapp.data.remote.dto.get

data class CartItemDTO (
    val cartItemId: Int? = 0,
    val maGioHang: Int? = 0,
    val detailProduct: ProductChiTietDTO? = null,
    val soLuong: Int? = 0,
    val giamGia: Int? = 0
)