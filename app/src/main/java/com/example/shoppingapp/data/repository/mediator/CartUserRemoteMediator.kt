package com.example.shoppingapp.data.repository.mediator

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.data.local.db.AppDatabase
import com.example.shoppingapp.data.local.entity.CartEntity
import com.example.shoppingapp.data.local.entity.RemoteKeyEntity
import com.example.shoppingapp.data.local.mapper.CartMapper
import com.example.shoppingapp.data.remote.api.CartAPI
import com.example.shoppingapp.domain.model.CartItemInCart

@OptIn(ExperimentalPagingApi::class)
class CartUserRemoteMediator(
    private val cartAPI: CartAPI,
    private val appDatabase: AppDatabase,
    private val idUser: String,
    private val queryKey: String
) : RemoteMediator<Int, CartItemInCart>() {

    private val cartDao = appDatabase.cartDao()
    private val cartItemDao = appDatabase.cartItemDao()
    private val remoteKeyDAO = appDatabase.remoteKeyDao()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CartItemInCart>
    ): MediatorResult {
        return try {
            val remoteKey = remoteKeyDAO.getRemoteKey(queryKey)

            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> remoteKey?.prevKey ?: return MediatorResult.Success(true)
                LoadType.APPEND -> remoteKey?.nextKey ?: return MediatorResult.Success(true)
            }

            val response = cartAPI.getCartUser(idUser, page)
            val dataResponse = response.body()
            Log.e("Cartentity", "load: dto ${dataResponse?.contents}")
            val endOfPagination =
                dataResponse?.contents?.isEmpty() == true || dataResponse?.contents?.size!! < StringConstants.PAGE_SIZE

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    cartDao.clearCartUser(idUser)
                    remoteKeyDAO.clearByQueryKey(queryKey)
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1

                dataResponse?.contents?.forEach { cartDTO ->
                    cartDao.insert(CartMapper.mapToCartEntity(cartDTO))
                    cartItemDao.insertAll(cartDTO.cartItemDTOS.map { cartItemDTO ->
                        CartMapper.mapToCartItemEntity(
                            cartItemDTO,
                            cartDTO.cartId!!
                        )
                    })
                }

                remoteKeyDAO.insert(
                    RemoteKeyEntity(
                        queryKey,
                        prevKey,
                        nextKey
                    )
                )

            }

            MediatorResult.Success(endOfPagination)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }

    }

}