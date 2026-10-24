package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.PageResponse
import com.example.shoppingapp.data.remote.dto.get.ProductReviewListItemResponse
import com.example.shoppingapp.data.remote.dto.get.ProductRatingStatsResponse
import com.example.shoppingapp.data.remote.dto.get.CreateReviewResponse
import com.example.shoppingapp.data.remote.dto.get.DeleteReviewResponse
import com.example.shoppingapp.data.remote.dto.get.ProductReviewResponse
import com.example.shoppingapp.data.remote.dto.get.UpdateReviewResponse
import com.example.shoppingapp.data.remote.dto.request.CreateReviewRequest
import com.example.shoppingapp.data.remote.dto.request.UpdateReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Path

interface ReviewAPI {
    @retrofit2.http.Multipart
    @POST("reviews/images")
    suspend fun uploadImage(@retrofit2.http.Part file: okhttp3.MultipartBody.Part): Response<com.example.shoppingapp.data.remote.dto.get.ReviewImageUploadResponse>

    @GET("reviews/product/{productId}")
    suspend fun getProductReviews(
        @Path("productId") productId: Int,
        @Query("pageNumber") pageNumber: Int
    ): Response<PageResponse<ProductReviewListItemResponse>>

    @GET("reviews/product/{productId}/stats")
    suspend fun getProductRatingStats(@Path("productId") productId: Int): Response<ProductRatingStatsResponse>

    @POST("reviews")
    suspend fun postReview(@Body createReviewRequest: CreateReviewRequest): Response<CreateReviewResponse>

    @GET("reviews/{reviewId}")
    suspend fun getProductReviewDetail(@Path("reviewId") reviewId: Int): Response<ProductReviewResponse>

    @PATCH("reviews/{reviewId}")
    suspend fun updateReview(
        @Path("reviewId") reviewId: Int,
        @Body updateReviewRequest: UpdateReviewRequest
    ): Response<UpdateReviewResponse>

    @DELETE("reviews/{reviewId}")
    suspend fun deleteReview(@Path("reviewId") reviewId: Int): Response<DeleteReviewResponse>
}
