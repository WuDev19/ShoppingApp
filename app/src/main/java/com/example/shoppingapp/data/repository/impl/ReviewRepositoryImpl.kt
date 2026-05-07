package com.example.shoppingapp.data.repository.impl

import android.net.Uri
import com.example.shoppingapp.data.remote.api.ReviewAPI
import com.example.shoppingapp.data.remote.dto.request.CreateReviewRequest
import com.example.shoppingapp.data.remote.helper.NetworkHelper
import com.example.shoppingapp.data.remote.mapper.ReviewMapper
import com.example.shoppingapp.data.source.cloudinary.CloudinaryUploader
import com.example.shoppingapp.di.qualifier.ImageReviewUploader
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.repository.review.ReviewRepository
import com.example.shoppingapp.ui.state.StateListener
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewAPI: ReviewAPI,
    private val moshi: Moshi,
    @param:ImageReviewUploader private val uploader: CloudinaryUploader

) : ReviewRepository {
    override suspend fun postReview(createReviewRequest: CreateReviewRequest): StateListener<Unit> =
        try {
            NetworkHelper.callApi(
                apiCall = {
                    reviewAPI.postReview(createReviewRequest)
                },
                mapper = {
                },
                moshi = moshi
            )
        } catch (e: Exception) {
            StateListener.Error(e)
        }

    override fun uploadImageReview(uri: Uri): Flow<StateListener<String>> = flow {
        emit(StateListener.Loading)
        val url = uploader.upload(uri)
        emit(StateListener.Success(url))
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

    override suspend fun getProductReviewDetail(reviewId: Int): StateListener<ProductReview> = try {
        NetworkHelper.callApi(
            apiCall = {
                reviewAPI.getProductReviewDetail(reviewId)
            },
            mapper = { productReviewResponse ->
                ReviewMapper.mapToProductReview(productReviewResponse)
            },
            moshi
        )
    } catch (e: Exception) {
        StateListener.Error(e)
    }
}