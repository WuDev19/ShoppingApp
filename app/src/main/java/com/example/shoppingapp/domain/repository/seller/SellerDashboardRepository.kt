package com.example.shoppingapp.domain.repository.seller

import com.example.shoppingapp.domain.model.seller.SellerDashboard
import com.example.shoppingapp.domain.model.seller.SellerMonthlyStats

interface SellerDashboardRepository {
    suspend fun dashboard(fromDate: String, toDate: String): SellerDashboard
    suspend fun monthly(year: Int): SellerMonthlyStats
}
