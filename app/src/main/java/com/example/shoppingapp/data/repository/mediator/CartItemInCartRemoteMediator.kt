package com.example.shoppingapp.data.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.data.local.db.AppDatabase
import com.example.shoppingapp.data.local.entity.CartItemEntity
import com.example.shoppingapp.data.local.entity.RemoteKeyEntity
import com.example.shoppingapp.data.remote.api.CartAPI

@OptIn(ExperimentalPagingApi::class)
class CartItemInCartRemoteMediator(
    private val cartAPI: CartAPI,
    private val appDatabase: AppDatabase,
    private val idCart: Int,
    private val queryKey: String
) : RemoteMediator<Int, CartItemEntity>() {

    private val cartItemDAO = appDatabase.cartItemDao()
    private val remoteKeyDAO = appDatabase.remoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CartItemEntity>
    ): MediatorResult {
        return try {
            val remoteKey = remoteKeyDAO.getRemoteKey(queryKey)
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> remoteKey?.prevKey ?: return MediatorResult.Success(true)
                LoadType.APPEND -> remoteKey?.nextKey ?: return MediatorResult.Success(true)
            }

            val response = cartAPI.getCartItemInCart(idCart, page)
            val dataResponse = response.body()
            val endOfPagination =
                dataResponse?.contents?.isEmpty() == true || dataResponse?.contents?.size!! < StringConstants.PAGE_SIZE

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    cartItemDAO.clearCartItemInCartOfUser(idCart)
                    remoteKeyDAO.clearByQueryKey(queryKey)
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1

                val listCartItemEntity = dataResponse?.contents?.map {
                    CartItemEntity(
                        it.cartItemId ?: 0,
                        it.maGioHang ?: 0,
                        it.detailProduct?.maSanPhamDeTail ?: 0,
                        it.detailProduct?.tenDetail ?: "",
                        it.detailProduct?.inStock ?: 0,
                        it.detailProduct?.anhDetail ?: "",
                        it.detailProduct?.unitPrice ?: 0,
                        it.detailProduct?.color ?: "",
                        it.detailProduct?.kichco ?: "",
                        it.detailProduct?.maSp ?: 0,
                        it.soLuong ?: 0,
                        it.giamGia ?: 0,
                        1
                    )
                }

                listCartItemEntity?.let { cartItemDAO.insertAll(it) }

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