package com.example.shoppingapp.data.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.local.db.AppDatabase
import com.example.shoppingapp.data.local.entity.ProductEntity
import com.example.shoppingapp.data.local.entity.RemoteKeyEntity
import com.example.shoppingapp.data.remote.api.ProductAPI

@OptIn(ExperimentalPagingApi::class)
class AllProductOfShopRemoteMediator(
    private val productAPI: ProductAPI,
    private val appDatabase: AppDatabase,
    private val queryKey: String,
    private val idShop: Int
) : RemoteMediator<Int, ProductEntity>() {

    private val productDAO = appDatabase.productDao()
    private val remoteKeyDao = appDatabase.remoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {

            val keyEntity = remoteKeyDao.getRemoteKey(queryKey)

            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> keyEntity?.prevKey ?: return MediatorResult.Success(true)
                LoadType.APPEND -> keyEntity?.nextKey ?: return MediatorResult.Success(true)
            }

            val response = productAPI.getOtherProductsOfShop(idShop, page)
            val listProductDTO = response.body()
            val endOfPagination = listProductDTO?.contents?.isEmpty() == true ||
                                    listProductDTO?.contents?.size!! < StringConstants.PAGE_SIZE

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    productDAO.clearProductOfShop(idShop)
                    remoteKeyDao.clearByQueryKey(queryKey)
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1

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
                listProductEntity?.let { productDAO.insertAll(it) }

                remoteKeyDao.insert(
                    RemoteKeyEntity(
                        queryKey,
                        prevKey,
                        nextKey
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = endOfPagination)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

}