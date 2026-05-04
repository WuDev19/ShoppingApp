package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.CreateReviewResponse
import com.example.shoppingapp.data.remote.dto.request.CreateReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReviewAPI {

    @POST("reviews")
    suspend fun postReview(@Body createReviewRequest: CreateReviewRequest): Response<CreateReviewResponse>
}