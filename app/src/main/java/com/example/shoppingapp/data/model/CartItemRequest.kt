package com.example.shoppingapp.data.model

data class CartItemRequest(
    var maSanPhamDetail: Int,
    var soluong: Int,
    var giamGia: Int? = null,
)
