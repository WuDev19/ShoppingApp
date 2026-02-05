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
import com.example.shoppingapp.data.remote.api.product.GetProductAPI

@OptIn(ExperimentalPagingApi::class)
class BestSellerProductShopRemoteMediator(
    private val getProductAPI: GetProductAPI,
    private val appDatabase: AppDatabase,
    private val idSeller: Int,
    private val queryKey: String
) : RemoteMediator<Int, ProductEntity>() {

    private val productDAO = appDatabase.productDao()
    private val remoteKeyDAO = appDatabase.remoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val remoteKey = remoteKeyDAO.getRemoteKey(queryKey)
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> remoteKey?.prevKey ?: return MediatorResult.Success(true)
                LoadType.APPEND -> remoteKey?.nextKey ?: return MediatorResult.Success(true)
            }

            val response = getProductAPI.getBestSellerProductShop(idSeller, page)
            val dataResponse = response.body()
            val endOfPagination =
                dataResponse?.contents?.isEmpty() == true || dataResponse?.contents?.size!! < StringConstants.PAGE_SIZE

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDAO.clearByQueryKey(queryKey)
                    productDAO.clearBestSellerProductOfShop(idSeller)
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1

                val listProduct = dataResponse?.contents?.map { productDTO ->
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

                listProduct?.let { productDAO.insertAll(it) }

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