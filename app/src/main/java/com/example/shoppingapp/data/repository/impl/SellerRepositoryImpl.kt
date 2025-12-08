package com.example.shoppingapp.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.mapper.SellerMapper
import com.example.shoppingapp.data.remote.api.SellerAPI
import com.example.shoppingapp.data.paging_source.PagingSourceSeller
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(private val sellerAPI: SellerAPI) : SellerRepository {

    override fun getAllSeller(): Flow<PagingData<SellerInStore>> {
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceSeller(sellerAPI)
            }
        ).flow
    }

    override fun getSellerById(id: Int): Flow<StateListener<SellerInStore>> = flow {
        try {
            val response = sellerAPI.getSellerById(id)
            if(response.isSuccessful){
                val sellerDTO = response.body()
                val sellerInStore = sellerDTO?.let {
                    SellerMapper.mapToSellerInStore(it)
                }
                sellerInStore?.let {
                    emit(StateListener.Success(it))
                } ?: emit(StateListener.Error(Exception("Cast Error")))
            }
            else{
                emit(StateListener.Error(Exception(response.message())))
            }
        }
        catch (e: Exception){
            emit(StateListener.Error(e))
        }
    }.flowOn(Dispatchers.IO)

}