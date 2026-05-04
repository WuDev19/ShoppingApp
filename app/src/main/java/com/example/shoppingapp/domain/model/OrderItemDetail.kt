package com.example.shoppingapp.domain.model

import com.example.shoppingapp.domain.model.product.ProductChiTiet

data class OrderItemDetail(
    val orderItemId: Int,
    val detailProduct: ProductChiTiet,
    val number: Int,
    val price: Int,
    val reviewId: Int?
)
