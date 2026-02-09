package com.example.shoppingapp.data.remote.dto.request

data class PostProductDetailRequest(
    val productNameDetail: String,
    val inStock: Int,
    var imageDetail: String?,
    val unitPrice: Int,
    val color: String?,
    val size: String?
)
