package com.example.shoppingapp.data.remote.dto.request

data class CreateReviewRequest(
    val userId: String,
    val productId: Int,
    val orderItemId: Int,
    val rate: Int?,
    val comment: String?,
    val urls: List<String>
)
