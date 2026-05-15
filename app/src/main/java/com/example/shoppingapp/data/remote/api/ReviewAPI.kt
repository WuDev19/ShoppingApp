package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.CreateReviewResponse
import com.example.shoppingapp.data.remote.dto.get.ProductReviewResponse
import com.example.shoppingapp.data.remote.dto.get.UpdateReviewResponse
import com.example.shoppingapp.data.remote.dto.request.CreateReviewRequest
import com.example.shoppingapp.data.remote.dto.request.UpdateReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewAPI {

    @POST("reviews")
    suspend fun postReview(@Body createReviewRequest: CreateReviewRequest): Response<CreateReviewResponse>

    @GET("reviews/{reviewId}")
    suspend fun getProductReviewDetail(@Path("reviewId") reviewId: Int): Response<ProductReviewResponse>

    @PATCH("reviews/{reviewId}")
    suspend fun updateReview(
        @Path("reviewId") reviewId: Int,
        @Body updateReviewRequest: UpdateReviewRequest
    ): Response<UpdateReviewResponse>

}