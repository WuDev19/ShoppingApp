package com.example.shoppingapp.data.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.shoppingapp.data.local.db.AppDatabase
import com.example.shoppingapp.data.local.entity.CartEntity
import com.example.shoppingapp.data.remote.api.CartAPI

@OptIn(ExperimentalPagingApi::class)
class CartUserRemoteMediator(
    private val cartAPI: CartAPI,
    private val appDatabase: AppDatabase,
    private val idUser: Int,
    private val queryKey: String
) : RemoteMediator<Int, CartEntity>() {

    private val cartDao = appDatabase.cartDao()
    private val remoteKeyDAO = appDatabase.remoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CartEntity>
    ): MediatorResult {
        TODO("Not yet implemented")
    }
}