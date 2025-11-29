package com.example.shoppingapp.data.remote.dto

data class CartItemRequest(
    var maSanPhamDetail: Int,
    var soluong: Int,
    var giamGia: Int? = null,
)
