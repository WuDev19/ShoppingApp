package com.example.shoppingapp.ui.state

import com.example.shoppingapp.domain.model.seller.DashboardFilter
import com.example.shoppingapp.domain.model.seller.SellerDashboard
import com.example.shoppingapp.domain.model.seller.SellerMonthlyStats

enum class DashboardError { NETWORK, ACCESS, SERVER }
data class DashboardSection<T>(val data: T? = null, val loading: Boolean = false, val error: DashboardError? = null)
data class SellerDashboardState(
    val filter: DashboardFilter,
    val summary: DashboardSection<SellerDashboard> = DashboardSection(),
    val monthly: DashboardSection<SellerMonthlyStats> = DashboardSection(),
    val invalidFilter: Boolean = false,
    val signedIn: Boolean = false
)
