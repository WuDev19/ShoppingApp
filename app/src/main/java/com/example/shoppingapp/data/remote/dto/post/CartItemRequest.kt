package com.example.shoppingapp.data.remote.dto.post

data class CartItemRequest(
    var maSanPhamDetail: Int,
    var soluong: Int,
    var giamGia: Int? = null,
)
