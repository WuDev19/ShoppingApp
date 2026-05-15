package com.example.shoppingapp.data.remote.dto.request

data class UpdateReviewRequest(
    val comment: String,
    val rate: Int,
    val deleteUrlId: List<Int>,
    val newUrl: List<String>,
)