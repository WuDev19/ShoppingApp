package com.example.shoppingapp.domain.model

data class CartItem (
    val cartItemId: Int? = 0,
    val maGioHang: Int? = 0,
    val detailProduct: ProductChiTiet? = null,
    val soLuong: Int? = 0,
    val giamGia: Int? = 0
)