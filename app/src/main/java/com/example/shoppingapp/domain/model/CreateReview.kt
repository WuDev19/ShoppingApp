package com.example.shoppingapp.domain.model

data class CreateReview(
    var userId: String?,
    val productId: Int,
    val orderItemId: Int,
    val rate: Int?,
    val comment: String?,
    var urls: List<String>
)
