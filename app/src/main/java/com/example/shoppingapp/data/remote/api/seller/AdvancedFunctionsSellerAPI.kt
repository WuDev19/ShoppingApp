package com.example.shoppingapp.data.remote.api.seller

import retrofit2.Response
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface AdvancedFunctionsSellerAPI {

    @PATCH("seller/profile/{sellerId}")
    suspend fun editProfile(
        @Path("sellerId") sellerId: Int,
        @QueryMap profile: Map<String, String>
    ): Response<Unit>

}