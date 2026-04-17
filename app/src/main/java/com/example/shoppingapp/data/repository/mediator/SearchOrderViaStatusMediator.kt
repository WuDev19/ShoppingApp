package com.example.shoppingapp.data.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.shoppingapp.data.local.db.AppDatabase
import com.example.shoppingapp.data.local.entity.OrderEntity
import com.example.shoppingapp.data.local.entity.OrderItemEntity
import com.example.shoppingapp.data.local.entity.ProductDetailEntity
import com.example.shoppingapp.data.local.entity.RemoteKeyEntity
import com.example.shoppingapp.data.local.pojo.OrderWithOrderItemAndProduct
import com.example.shoppingapp.data.remote.api.OrderAPI
import com.example.shoppingapp.data.remote.mapper.OrderMapper
import com.example.shoppingapp.utils.StringConstants

@OptIn(ExperimentalPagingApi::class)
class SearchOrderViaStatusMediator(
    private val orderAPI: OrderAPI,
    private val appDatabase: AppDatabase,
    private val queryKey: String,
    private val userId: String,
    private val status: String,
    private val keyword: String
) : RemoteMediator<Int, OrderWithOrderItemAndProduct>() {

    private val orderDAO = appDatabase.orderDao()
    private val orderItemDAO = appDatabase.orderItemDao()
    private val productDetailDAO = appDatabase.productDetailDao()
    private val remoteKeyDAO = appDatabase.remoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, OrderWithOrderItemAndProduct>
    ): MediatorResult {
        return try {
            val remoteKey = remoteKeyDAO.getRemoteKey(queryKey)
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.APPEND -> remoteKey?.nextKey ?: return MediatorResult.Success(true)
                LoadType.PREPEND -> remoteKey?.prevKey ?: return MediatorResult.Success(true)
            }

            val response = orderAPI.searchOrder(userId, status, page, keyword)
            val dataResponse = response.body()?.contents ?: emptyList()
            val endOfPagination =
                dataResponse.isEmpty() || dataResponse.size < StringConstants.PAGE_SIZE
            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDAO.clearByQueryKey(queryKey)
                }
                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1
                if (dataResponse.isNotEmpty()) {
                    val orderEntities = mutableListOf<OrderEntity>()
                    val orderItemEntities = mutableListOf<OrderItemEntity>()
                    val productDetailEntities = mutableListOf<ProductDetailEntity>()
                    dataResponse.forEach { orderDetailDTO ->
                        orderEntities.add(OrderMapper.mapToOrderEntity(orderDetailDTO))
                        orderDetailDTO.orderItems.forEach { orderItemDetailDTO ->
                            orderItemEntities.add(
                                OrderMapper.mapToOrderItemEntity(
                                    orderDetailDTO.orderId,
                                    orderItemDetailDTO
                                )
                            )
                            productDetailEntities.add(
                                OrderMapper.mapToProductDetailEntity(
                                    orderItemDetailDTO.detailProduct
                                )
                            )
                        }
                    }
                    orderDAO.insertAll(orderEntities)
                    productDetailDAO.insertAll(productDetailEntities)
                    orderItemDAO.insertAll(orderItemEntities)
                    remoteKeyDAO.insert(RemoteKeyEntity(queryKey, prevKey, nextKey))
                }
            }
            MediatorResult.Success(endOfPagination)
        } catch (e: Exception) {
            return MediatorResult.Error(Exception(e.message))
        }
    }

}