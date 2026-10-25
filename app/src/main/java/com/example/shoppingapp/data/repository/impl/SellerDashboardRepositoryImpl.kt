package com.example.shoppingapp.data.repository.impl

import com.example.shoppingapp.data.remote.api.seller.SellerDashboardAPI
import com.example.shoppingapp.data.remote.mapper.toDomain
import com.example.shoppingapp.domain.repository.seller.SellerDashboardRepository
import com.example.shoppingapp.utils.StringConstants
import retrofit2.HttpException
import javax.inject.Inject

class SellerDashboardRepositoryImpl @Inject constructor(private val api: SellerDashboardAPI) : SellerDashboardRepository {
    override suspend fun dashboard(fromDate: String, toDate: String) = api.dashboard(fromDate, toDate).let {
        if (!it.isSuccessful) throw HttpException(it)
        requireNotNull(it.body()) { StringConstants.API_ERROR }.toDomain()
    }
    override suspend fun monthly(year: Int) = api.monthly(year).let {
        if (!it.isSuccessful) throw HttpException(it)
        requireNotNull(it.body()) { StringConstants.API_ERROR }.toDomain()
    }
}
