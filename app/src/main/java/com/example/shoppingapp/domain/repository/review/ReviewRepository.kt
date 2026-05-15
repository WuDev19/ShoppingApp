package com.example.shoppingapp.domain.repository.review

import android.net.Uri
import com.example.shoppingapp.domain.model.CreateReview
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.model.UpdateReview
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {

    suspend fun postReview(createReview: CreateReview): StateListener<Unit>
    fun uploadImageReview(uri: Uri): Flow<StateListener<String>>
    suspend fun getProductReviewDetail(reviewId: Int): StateListener<ProductReview>
    suspend fun updateReview(
        reviewId: Int,
        updateReview: UpdateReview
    ): StateListener<String>
}