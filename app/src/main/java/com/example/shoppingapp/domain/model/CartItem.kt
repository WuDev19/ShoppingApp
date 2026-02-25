package com.example.shoppingapp.domain.model

import com.example.shoppingapp.domain.model.product.ProductChiTiet
data class CartItem(
    val cartItemId: Int? = 0,
    val maGioHang: Int? = 0,
    val detailProduct: ProductChiTiet? = null,
    val soLuong: Int? = 0,
    val giamGia: Int? = 0,
    val cartId: Int? = 0,
) : CartUiModel()