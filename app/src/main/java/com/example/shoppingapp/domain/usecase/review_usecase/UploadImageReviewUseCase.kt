package com.example.shoppingapp.domain.usecase.review_usecase

import android.net.Uri
import com.example.shoppingapp.domain.repository.review.ReviewRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadImageReviewUseCase @Inject constructor(private val repo: ReviewRepository) {
    operator fun invoke(uri: Uri): Flow<StateListener<String>> = repo.uploadImageReview(uri)
}