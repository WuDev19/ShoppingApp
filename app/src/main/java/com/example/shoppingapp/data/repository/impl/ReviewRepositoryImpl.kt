package com.example.shoppingapp.data.repository.impl

import com.example.shoppingapp.domain.model.ProductReviewPage
import kotlinx.coroutines.CancellationException
import android.net.Uri
import com.example.shoppingapp.data.remote.api.ReviewAPI
import com.example.shoppingapp.data.remote.helper.NetworkHelper
import com.example.shoppingapp.data.remote.mapper.ReviewMapper
import com.example.shoppingapp.data.source.cloudinary.CloudinaryUploader
import com.example.shoppingapp.di.qualifier.ImageReviewUploader
import com.example.shoppingapp.domain.model.CreateReview
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.model.UpdateReview
import com.example.shoppingapp.domain.repository.review.ReviewRepository
import com.example.shoppingapp.ui.state.StateListener
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named

class ReviewRepositoryImpl @Inject constructor(
    private val reviewAPI: ReviewAPI,
    private val moshi: Moshi,
    @param:ImageReviewUploader private val uploader: CloudinaryUploader,
    @param:Named("userId") private val userId: String,
) : ReviewRepository {
    override suspend fun getProductReviews(productId: Int, page: Int) = NetworkHelper.callApi(
        apiCall = { reviewAPI.getProductReviews(productId, page) },
        mapper = { response ->
            ProductReviewPage(response.pageNumber,
                response.contents.orEmpty().map(ReviewMapper::mapToProductReviewItem))
        }, moshi = moshi
    )

    override suspend fun getProductRatingStats(productId: Int) = NetworkHelper.callApi(
        apiCall = { reviewAPI.getProductRatingStats(productId) },
        mapper = ReviewMapper::mapToProductRatingStats, moshi = moshi
    )

    override suspend fun postReview(createReview: CreateReview): StateListener<Unit> {
        val createReviewRequest = ReviewMapper.mapToCreateReviewRequest(userId, createReview)
        return try {
            NetworkHelper.callApi(
                apiCall = {
                    reviewAPI.postReview(createReviewRequest)
                },
                mapper = {
                },
                moshi = moshi
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            StateListener.Error(e)
        }
    }

    override fun uploadImageReview(uri: Uri): Flow<StateListener<String>> = flow {
        emit(StateListener.Loading)
        val url = uploader.upload(uri)
        emit(StateListener.Success(url))
    }.catch {
        if (it is CancellationException) throw it
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
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        StateListener.Error(e)
    }

    override suspend fun updateReview(
        reviewId: Int,
        updateReview: UpdateReview
    ): StateListener<String> {
        val updateRequest = ReviewMapper.mapToUpdateReviewRequest(updateReview)
        return try {
            NetworkHelper.callApi(
                apiCall = {
                    reviewAPI.updateReview(reviewId, updateRequest)
                },
                mapper = { updateResponse ->
                    updateResponse.updated_at
                },
                moshi = moshi
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            StateListener.Error(e)
        }
    }

    override suspend fun deleteReview(reviewId: Int): StateListener<String> = try {

        NetworkHelper.callApi(
            apiCall = {
                reviewAPI.deleteReview(reviewId)
            },
            mapper = { deleteResponse ->
                deleteResponse.deleted_at
            },
            moshi
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        StateListener.Error(e)
    }
}
