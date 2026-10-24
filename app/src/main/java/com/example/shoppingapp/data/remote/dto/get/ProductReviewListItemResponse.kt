package com.example.shoppingapp.data.remote.dto.get

data class ProductReviewListItemResponse(
    val reviewId: Int, val rating: Int, val comment: String?,
    val createdAt: Long, val updatedAt: Long, val productId: Int?,
    val userId: String?, val userName: String?, val userAvatar: String?,
    val images: List<ReviewImageResponse> = emptyList()
)
data class ProductRatingStatsResponse(
    val averageRating: Double, val totalReviews: Long,
    val fiveStar: Long, val fourStar: Long, val threeStar: Long,
    val twoStar: Long, val oneStar: Long
)
