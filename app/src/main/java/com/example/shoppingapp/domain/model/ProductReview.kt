package com.example.shoppingapp.domain.model

data class ProductReview(
    val reviewId: Int,
    val rating: Int,
    val comment: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isDelete: Boolean,
    val productId: Int,
    val images: List<ReviewImage>
)