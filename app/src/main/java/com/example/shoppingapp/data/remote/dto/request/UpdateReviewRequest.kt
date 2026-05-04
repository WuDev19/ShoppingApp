package com.example.shoppingapp.data.remote.dto.request

data class UpdateReviewRequest(
    val comment: String,
    val rate: Int,
    val urlId: Int,
    val newUrl: List<String>,
    val isDeleteImage: Boolean
)