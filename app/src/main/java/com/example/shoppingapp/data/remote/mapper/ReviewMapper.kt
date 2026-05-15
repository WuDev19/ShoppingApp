package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.get.ProductReviewResponse
import com.example.shoppingapp.data.remote.dto.get.ReviewImageResponse
import com.example.shoppingapp.data.remote.dto.request.CreateReviewRequest
import com.example.shoppingapp.data.remote.dto.request.UpdateReviewRequest
import com.example.shoppingapp.domain.model.CreateReview
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.model.ReviewImage
import com.example.shoppingapp.domain.model.UpdateReview

object ReviewMapper {

    fun mapToProductReview(productReviewResponse: ProductReviewResponse): ProductReview {
        val images = productReviewResponse.images.map { mapToReviewImage(it) }
        return ProductReview(
            productReviewResponse.reviewId,
            productReviewResponse.rating,
            productReviewResponse.comment,
            productReviewResponse.createdAt,
            productReviewResponse.updatedAt,
            productReviewResponse.isDelete,
            productReviewResponse.productId,
            images
        )
    }

    fun mapToReviewImage(reviewImageResponse: ReviewImageResponse): ReviewImage {
        return ReviewImage(reviewImageResponse.imageId, reviewImageResponse.imageUrl)
    }

    fun mapToUpdateReviewRequest(review: UpdateReview): UpdateReviewRequest = UpdateReviewRequest(
        review.comment,
        review.rate,
        review.deletingImageReviews,
        review.newImageUrls
    )

    fun mapToCreateReviewRequest(userId: String, createReview: CreateReview): CreateReviewRequest =
        CreateReviewRequest(
            userId,
            createReview.productId,
            createReview.orderItemId,
            createReview.rate,
            createReview.comment,
            createReview.urls
        )
}