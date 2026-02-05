package com.example.shoppingapp.data.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.local.db.AppDatabase
import com.example.shoppingapp.data.local.entity.CategoryAndProductEntity
import com.example.shoppingapp.data.local.entity.RemoteKeyEntity
import com.example.shoppingapp.data.remote.api.product.GetProductAPI

@OptIn(ExperimentalPagingApi::class)
class CategoryAndProductShopRemoteMediator(
    private val getProductAPI: GetProductAPI,
    private val appDatabase: AppDatabase,
    private val queryKey: String,
    private val idSeller: Int,
    private val tenDanhMuc: String
) : RemoteMediator<Int, CategoryAndProductEntity>(){

    private val categoryAndProductDAO = appDatabase.categoryAndProductDao()
    private val remoteKeyDAO = appDatabase.remoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CategoryAndProductEntity>
    ): MediatorResult {
        return try {
            val remoteKey = remoteKeyDAO.getRemoteKey(queryKey)
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> remoteKey?.prevKey ?: return MediatorResult.Success(true)
                LoadType.APPEND -> remoteKey?.nextKey ?: return MediatorResult.Success(true)
            }

            val response = getProductAPI.getCategoryAndProductByShop(idSeller, tenDanhMuc, page)
            val dataResponse = response.body()
            val endOfPagination = dataResponse?.contents?.isEmpty() == true || dataResponse?.contents?.size!! < StringConstants.PAGE_SIZE

            appDatabase.withTransaction {
                if(loadType == LoadType.REFRESH){
                    remoteKeyDAO.clearByQueryKey(queryKey)
                    categoryAndProductDAO.clearCategoryAndProductShop(idSeller, tenDanhMuc)
                }

                val prevKey = if(page == 0) null else page - 1
                val nextKey = if(endOfPagination) null else page + 1

                val listCateAndProEntity = dataResponse?.contents?.map { categoryProductDTO ->
                    CategoryAndProductEntity(
                        categoryProductDTO.maSp ?: 0,
                        categoryProductDTO.tenShop ?: "",
                        categoryProductDTO.giaSp ?: 0,
                        categoryProductDTO.moTaSp ?: "",
                        categoryProductDTO.thumbnail ?: "",
                        categoryProductDTO.giamGia ?: 0,
                        categoryProductDTO.numberBought ?: 0,
                        categoryProductDTO.maNguoiBan ?: 0,
                        categoryProductDTO.tenDanhMuc ?: ""
                    )
                }

                listCateAndProEntity?.let { categoryAndProductDAO.insertAll(it) }
                remoteKeyDAO.insert(
                    RemoteKeyEntity(
                        queryKey,
                        prevKey,
                        nextKey
                    )
                )

            }
            MediatorResult.Success(endOfPagination)
        }
        catch (e: Exception){
            MediatorResult.Error(e)
        }
    }

}