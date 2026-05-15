package com.example.shoppingapp.domain.usecase.review_usecase

import com.example.shoppingapp.domain.model.CreateReview
import com.example.shoppingapp.domain.repository.review.ReviewRepository
import com.example.shoppingapp.ui.state.StateListener
import javax.inject.Inject

class CreateReviewUseCase @Inject constructor(
    private val repo: ReviewRepository,
) {
    suspend operator fun invoke(createReview: CreateReview): StateListener<Unit> {
        return repo.postReview( createReview)
    }
}