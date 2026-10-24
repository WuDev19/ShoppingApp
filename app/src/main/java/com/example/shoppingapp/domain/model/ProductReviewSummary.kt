package com.example.shoppingapp.domain.model

data class ProductReviewItem(
    val reviewId: Int, val rating: Int, val comment: String,
    val createdAt: Long, val updatedAt: Long, val userName: String,
    val userAvatar: String?, val images: List<ReviewImage>
)
data class ProductReviewPage(val pageNumber: Int, val items: List<ProductReviewItem>)
data class ProductRatingStats(
    val averageRating: Double, val totalReviews: Long,
    val fiveStar: Long, val fourStar: Long, val threeStar: Long,
    val twoStar: Long, val oneStar: Long
)
