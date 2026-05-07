package com.example.shoppingapp.domain.usecase.review_usecase

import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.repository.review.ReviewRepository
import com.example.shoppingapp.ui.state.StateListener
import javax.inject.Inject

class GetProductReviewDetailUseCase @Inject constructor(private val repo: ReviewRepository) {
    suspend operator fun invoke(reviewId: Int): StateListener<ProductReview> =
        repo.getProductReviewDetail(reviewId)
}