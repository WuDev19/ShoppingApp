package com.example.shoppingapp.domain.model

data class UpdateReview(
    val comment: String,
    val rate: Int,
    val deletingImageReviews: List<Int>,
    val newImageUrls: List<String>
)