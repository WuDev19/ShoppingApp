package com.example.shoppingapp.domain.usecase.review_usecase

import com.example.shoppingapp.domain.repository.review.ReviewRepository
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(private val repository: ReviewRepository) {
    suspend operator fun invoke(reviewId: Int) = repository.deleteReview(reviewId)
}