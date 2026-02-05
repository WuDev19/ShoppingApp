package com.example.shoppingapp.data.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.shoppingapp.data.local.db.AppDatabase
import com.example.shoppingapp.data.local.entity.ProductEntity
import com.example.shoppingapp.data.local.entity.RemoteKeyProduct
import com.example.shoppingapp.data.remote.api.product.GetProductAPI

@OptIn(ExperimentalPagingApi::class)
class SearchProductInShopRemoteMediator(
    private val getProductAPI: GetProductAPI,
    private val appDatabase: AppDatabase,
    private val id: Int,
    private val text: String
) : RemoteMediator<Int, ProductEntity>() {

    private val productDAO = appDatabase.productDao()
    private val remoteKeyProductDAO = appDatabase.remoteKeyProductDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKey?.nextKey?.minus(1) ?: 0
                }
                LoadType.PREPEND -> {
                    val remoteKey =
                        getRemoteKeyForFirstItem(state) ?: return MediatorResult.Success(true)
                    remoteKey.prevKey ?: return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    val remoteKey =
                        getRemoteKeyForLastItem(state) ?: return MediatorResult.Success(true)
                    remoteKey.nextKey ?: return MediatorResult.Success(true)
                }
            }

            val response = getProductAPI.timKiemSanPhamCuaShop(id, text, page)
            val listProductDTO = response.body()
            val listProductEntity = listProductDTO?.contents?.map { productDTO ->
                ProductEntity(
                    productDTO.maSp ?: 0,
                    productDTO.tenNguoiBan ?: "",
                    productDTO.giaSp ?: 0,
                    productDTO.motaSp ?: "",
                    productDTO.thumbnail ?: "",
                    productDTO.giamgia ?: 0,
                    productDTO.numberBought ?: 0,
                    productDTO.maNguoiBan ?: 0
                )
            }
            val endOfPaginationReached = listProductDTO?.contents?.isEmpty() == true
            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyProductDAO.clearAll()
                    productDAO.clearSearchProductShop(id, text)
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val remoteKeys = listProductEntity?.map { productEntity ->
                    RemoteKeyProduct(productEntity.id, prevKey, nextKey)
                }

                listProductEntity?.let { productDAO.insertAll(it) }
                remoteKeys?.let { remoteKeyProductDAO.insertAll(it) }

            }

            MediatorResult.Success(endOfPaginationReached)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ProductEntity>): RemoteKeyProduct? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                remoteKeyProductDAO.getRemoteKeyById(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ProductEntity>): RemoteKeyProduct? {
        return state.pages.firstOrNull {//lay page dau tien
            it.data.isNotEmpty()
        }?.data?.firstOrNull() //lay item dau tien trong list product cua page do
            ?.let { product ->
                remoteKeyProductDAO.getRemoteKeyById(product.id) //lay remote key
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ProductEntity>): RemoteKeyProduct? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()
            ?.let { product ->
                remoteKeyProductDAO.getRemoteKeyById(product.id)
            }
    }

}