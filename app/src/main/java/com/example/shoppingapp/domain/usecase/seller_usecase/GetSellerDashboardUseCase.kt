package com.example.shoppingapp.domain.usecase.seller_usecase

import com.example.shoppingapp.domain.model.seller.DashboardFilter
import com.example.shoppingapp.domain.repository.seller.SellerDashboardRepository
import com.example.shoppingapp.utils.DashboardConstants
import javax.inject.Inject

class GetSellerDashboardUseCase @Inject constructor(private val repository: SellerDashboardRepository) {
    suspend operator fun invoke(filter: DashboardFilter) = run {
        require(filter.isValid())
        repository.dashboard(filter.fromDate, filter.toDate)
    }
}
class GetSellerMonthlyStatsUseCase @Inject constructor(private val repository: SellerDashboardRepository) {
    suspend operator fun invoke(year: Int) = run {
        require(year in DashboardConstants.MIN_YEAR..DashboardConstants.MAX_YEAR)
        repository.monthly(year)
    }
}
