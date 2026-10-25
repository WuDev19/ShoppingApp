package com.example.shoppingapp.data.remote.api.seller

import com.example.shoppingapp.data.remote.dto.get.SellerDashboardDTO
import com.example.shoppingapp.data.remote.dto.get.SellerMonthlyStatsDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SellerDashboardAPI {
    @GET("seller/dashboard")
    suspend fun dashboard(@Query("fromDate") fromDate: String, @Query("toDate") toDate: String): Response<SellerDashboardDTO>
    @GET("seller/dashboard/monthly")
    suspend fun monthly(@Query("year") year: Int): Response<SellerMonthlyStatsDTO>
}
