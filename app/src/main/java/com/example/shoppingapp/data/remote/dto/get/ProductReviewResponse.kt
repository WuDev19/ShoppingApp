package com.example.shoppingapp.data.remote.dto.get

data class ProductReviewResponse(
    val reviewId: Int,
    val rating: Int,
    val comment: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDelete: Boolean,
    val productId: Int,
    val images: List<ReviewImageResponse>
)
