package com.example.shoppingapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shoppingapp.data.remote.api.SellerAPI
import com.example.shoppingapp.data.remote.paging.PagingSourceSeller
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(private val sellerAPI: SellerAPI) : SellerRepository {

    override fun getAllSeller(): Flow<PagingData<SellerInStore>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceSeller(sellerAPI)
            }
        ).flow
    }

}