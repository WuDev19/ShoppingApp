package com.example.shoppingapp.domain.repository.review

import android.net.Uri
import com.example.shoppingapp.data.remote.dto.request.CreateReviewRequest
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {

    suspend fun postReview(createReviewRequest: CreateReviewRequest): StateListener<Unit>
    fun uploadImageReview(uri: Uri): Flow<StateListener<String>>
    suspend fun getProductReviewDetail(reviewId: Int): StateListener<ProductReview>
}