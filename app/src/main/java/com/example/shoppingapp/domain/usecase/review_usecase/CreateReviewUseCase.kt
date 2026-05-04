package com.example.shoppingapp.domain.usecase.review_usecase

import com.example.shoppingapp.data.remote.dto.request.CreateReviewRequest
import com.example.shoppingapp.domain.model.CreateReview
import com.example.shoppingapp.domain.repository.review.ReviewRepository
import com.example.shoppingapp.ui.state.StateListener
import javax.inject.Inject
import javax.inject.Named

class CreateReviewUseCase @Inject constructor(
    private val repo: ReviewRepository,
    @param:Named("userId") private val userId: String
) {
    suspend operator fun invoke(createReview: CreateReview): StateListener<Unit> {
        val createReviewRequest = CreateReviewRequest(
            userId,
            createReview.productId,
            createReview.orderItemId,
            createReview.rate,
            createReview.comment,
            createReview.urls
        )
        return repo.postReview(createReviewRequest)
    }
}