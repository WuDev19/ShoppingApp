package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.get.ProductReviewResponse
import com.example.shoppingapp.data.remote.dto.get.ReviewImageResponse
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.model.ReviewImage

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
}