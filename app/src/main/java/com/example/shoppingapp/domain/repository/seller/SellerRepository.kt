package com.example.shoppingapp.domain.repository.seller

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface SellerRepository {

    fun getAllSeller(): Flow<PagingData<SellerInStore>>

    fun getSellerById(id: Int): Flow<StateListener<SellerInStore>>

}