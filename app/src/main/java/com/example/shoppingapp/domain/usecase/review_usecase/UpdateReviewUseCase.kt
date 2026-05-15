package com.example.shoppingapp.domain.usecase.review_usecase

import com.example.shoppingapp.domain.model.UpdateReview
import com.example.shoppingapp.domain.repository.review.ReviewRepository
import javax.inject.Inject

class UpdateReviewUseCase @Inject constructor(private val repo: ReviewRepository) {

    suspend operator fun invoke(reviewId: Int, updateReview: UpdateReview) =
        repo.updateReview(reviewId, updateReview)
}