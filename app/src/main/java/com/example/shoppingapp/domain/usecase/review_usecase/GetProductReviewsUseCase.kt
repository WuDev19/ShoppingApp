package com.example.shoppingapp.domain.usecase.review_usecase

import com.example.shoppingapp.domain.repository.review.ReviewRepository
import javax.inject.Inject

class GetProductReviewsUseCase @Inject constructor(private val repository: ReviewRepository) {
    suspend operator fun invoke(productId: Int, page: Int) = repository.getProductReviews(productId, page)
}
class GetProductRatingStatsUseCase @Inject constructor(private val repository: ReviewRepository) {
    suspend operator fun invoke(productId: Int) = repository.getProductRatingStats(productId)
}
