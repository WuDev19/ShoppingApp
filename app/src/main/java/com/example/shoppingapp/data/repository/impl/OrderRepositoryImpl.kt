package com.example.shoppingapp.data.repository.impl

import android.annotation.SuppressLint
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.shoppingapp.data.local.db.AppDatabase
import com.example.shoppingapp.data.remote.api.OrderAPI
import com.example.shoppingapp.data.remote.api.PaymentAPI
import com.example.shoppingapp.data.remote.dto.get.ErrorResponseDTO
import com.example.shoppingapp.data.remote.dto.request.CancelOrderRequest
import com.example.shoppingapp.data.remote.dto.request.OrderPaymentRequest
import com.example.shoppingapp.data.remote.dto.request.OrderRequest
import com.example.shoppingapp.data.remote.helper.NetworkHelper
import com.example.shoppingapp.data.remote.mapper.OrderMapper
import com.example.shoppingapp.data.repository.mediator.OrderViaStatusMediator
import com.example.shoppingapp.data.repository.mediator.SearchOrderViaStatusMediator
import com.example.shoppingapp.domain.model.AfterPayment
import com.example.shoppingapp.domain.model.CancelOrder
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.domain.model.OrderDetail
import com.example.shoppingapp.domain.model.OrderStatus
import com.example.shoppingapp.domain.model.OrderUiModel
import com.example.shoppingapp.domain.model.user.OrderUpdateStatus
import com.example.shoppingapp.domain.repository.order.OrderRepository
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.utils.StringConstants
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderAPI: OrderAPI,
    private val moshi: Moshi,
    private val paymentAPI: PaymentAPI,
    private val appDatabase: AppDatabase
) :
    OrderRepository {
    override suspend fun createOrder(
        shopGhnId: Int,
        orderRequest: OrderRequest
    ): StateListener<Order> =
        try {
            val response = orderAPI.createOrder(shopGhnId, orderRequest)
            if (response.isSuccessful) {
                val result = response.body()
                result?.let {
                    StateListener.Success(Order(it.orderId, it.amount))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            } else {
                val errorBody = response.errorBody()
                val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                errorBody?.string()?.let {
                    StateListener.Error(Exception(adapter.fromJson(it)?.message))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            }
        } catch (e: Exception) {
            StateListener.Error(e)
        }

    override suspend fun createPayment(orderPaymentRequest: OrderPaymentRequest): StateListener<AfterPayment> =
        try {
            val response = paymentAPI.createPayment(orderPaymentRequest)
            if (response.isSuccessful) {
                val result = response.body()
                result?.let {
                    StateListener.Success(AfterPayment(orderPaymentRequest.orderId, it.paymentUrl))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            } else {
                val errorBody = response.errorBody()
                val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                errorBody?.string()?.let {
                    StateListener.Error(Exception(adapter.fromJson(it)?.message))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            }
        } catch (e: Exception) {
            StateListener.Error(e)
        }

    @SuppressLint("NewApi")
    override suspend fun updateStatusOrderWhenPaymentSuccess(orderId: String): StateListener<Order> =
        try {
            val modifyTime = Instant.now().toEpochMilli()
            val response = orderAPI.updateStatusOrderWhenPaymentSuccess(orderId, modifyTime)
            if (response.isSuccessful) {
                val result = response.body()
                result?.let {
                    StateListener.Success(Order(it.orderId, it.amount))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            } else {
                val errorBody = response.errorBody()
                val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                errorBody?.string()?.let {
                    StateListener.Error(Exception(adapter.fromJson(it)?.message))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            }
        } catch (e: Exception) {
            StateListener.Error(e)
        }

    override suspend fun getOrderStatus(orderId: String): StateListener<OrderStatus> {
        return NetworkHelper.callApi(
            apiCall = {
                orderAPI.getOrderDetail(orderId)
            },
            mapper = {
                OrderMapper.mapToOrderStatus(it)
            },
            moshi
        )
    }

    override suspend fun getOrderDetail(orderId: String): StateListener<OrderDetail> {
        return NetworkHelper.callApi(
            apiCall = {
                orderAPI.getOrderDetail(orderId)
            },
            mapper = { orderDetailDTO ->
                OrderMapper.mapToOrderDetail(orderDetailDTO)
            },
            moshi
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getOrderViaStatus(
        userId: String,
        status: String
    ): Flow<PagingData<OrderUiModel>> {
        val queryKey = "order_via_status:userId:$userId:status:$status"
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                initialLoadSize = StringConstants.PAGE_SIZE
            ),
            pagingSourceFactory = {
                if (status != StringConstants.STATUS_CANCELLED) {
                    appDatabase.orderDao().getPagingSourceOrderViaStatus(
                        userId,
                        status
                    ) //viết thêm paging source cho trạng thái cancel (sắp xếp theo time cancel)
                } else {
                    appDatabase.orderDao().getPagingSourceOrderViaStatusCancel(userId, status)
                }
            },
            remoteMediator = OrderViaStatusMediator(
                orderAPI = orderAPI,
                appDatabase = appDatabase,
                queryKey = queryKey,
                status = status,
                userId = userId
            )
        ).flow.map { pagingData ->
            pagingData.map { order ->
                Log.e("Dataaaaa", "getOrderViaStatus: $order")
                OrderUiModel.OrderItemUser(
                    OrderUiModel.OrderOfShop( //tạo sẵn orderitem có hết thông tin của order với shop, vì sau khi map ở đây rồi thì sẽ bị mất info nếu ko lưu lại
                        order.orderPojo.shopId,
                        order.orderPojo.shopGhnId,
                        order.orderPojo.shopName,
                        order.orderPojo.shopAvt
                    ),
                    OrderUiModel.Order(
                        order.orderPojo.orderId,
                        order.orderPojo.ghnOrderId,
                        order.orderPojo.orderStatus,
                    ),
                    com.example.shoppingapp.data.local.mapper.OrderMapper.mapToOrderItemDetail(
                        order.orderItemPojo,
                        order.productDetailPojo
                    )
                )
            }.insertSeparators { before, after -> //logic them cai phan cach giua cac item
                //add shop
                if (after == null) return@insertSeparators null
                val beforeId = before?.orderOfShop?.shopId
                val afterId = after.orderOfShop.shopId
                if (beforeId != afterId) {
                    OrderUiModel.OrderOfShop( //copy lại
                        after.orderOfShop.shopId,
                        after.orderOfShop.shopGhnId,
                        after.orderOfShop.shopName,
                        after.orderOfShop.shopAvt
                    )
                } else null
            }.insertSeparators { before, after ->
                //add order
                if (after == null) return@insertSeparators null
                if (before != null &&
                    before is OrderUiModel.OrderOfShop &&
                    after is OrderUiModel.OrderItemUser
                ) {
                    return@insertSeparators OrderUiModel.Order(
                        after.order.orderId,
                        after.order.ghnOrderId,
                        after.order.orderStatus
                    )
                }
                if (before != null &&
                    before is OrderUiModel.OrderItemUser &&
                    after is OrderUiModel.OrderItemUser &&
                    before.order.orderId != after.order.orderId
                ) {
                    return@insertSeparators OrderUiModel.Order(
                        after.order.orderId,
                        after.order.ghnOrderId,
                        after.order.orderStatus
                    )
                }
                null
            }.insertSeparators { before, after ->
                //add nút review
                if (after == null &&
                    before != null &&
                    before is OrderUiModel.OrderItemUser
                ) return@insertSeparators OrderUiModel.OrderReviewOrCancel(
                    before.orderOfShop.shopId,
                    before.orderOfShop.shopGhnId,
                    before
                )
                if (after != null &&
                    before != null &&
                    before is OrderUiModel.OrderItemUser &&
                    after is OrderUiModel.OrderOfShop
                ) {
                    OrderUiModel.OrderReviewOrCancel(
                        before.orderOfShop.shopId,
                        before.orderOfShop.shopGhnId,
                        before
                    )
                } else if (
                    after != null &&
                    before != null &&
                    before is OrderUiModel.OrderItemUser &&
                    after is OrderUiModel.Order
                ) {
                    OrderUiModel.OrderReviewOrCancel(
                        before.orderOfShop.shopId,
                        before.orderOfShop.shopGhnId,
                        before
                    )
                } else if (
                    after != null &&
                    before != null &&
                    before is OrderUiModel.OrderItemUser &&
                    after is OrderUiModel.OrderItemUser
                ) {
                    OrderUiModel.OrderReviewOrCancel(
                        before.orderOfShop.shopId,
                        before.orderOfShop.shopGhnId,
                        before
                    )
                } else null
            }
        }
    }

    override suspend fun cancelOrder(
        orderId: String,
        cancelOrderRequest: CancelOrderRequest
    ): StateListener<CancelOrder> = try {
        NetworkHelper.callApi(
            apiCall = {
                orderAPI.cancelOrder(orderId, cancelOrderRequest)
            },
            mapper = { cancelOrderDto ->
                OrderMapper.mapToCancelOrder(cancelOrderDto)
            },
            moshi
        )
    } catch (e: Exception) {
        StateListener.Error(e)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun searchOrder(
        userId: String,
        status: String,
        keyword: String,
    ): Flow<PagingData<OrderUiModel>> {
        Log.e(StringConstants.ORDER_ID, "searchOrder: ")
        val queryKey = "search_order:userId:$userId:status:$status:keyword:$keyword"
        return Pager(
            config = PagingConfig(
                pageSize = 6
            ),
            pagingSourceFactory = {
                if (status == StringConstants.STATUS_CANCELLED)
                    appDatabase.orderDao().searchOrderCancelPagingSource(userId, status, keyword)
                else
                    appDatabase.orderDao().searchOrderPagingSource(userId, status, keyword)
            },
            remoteMediator = SearchOrderViaStatusMediator(
                orderAPI,
                appDatabase,
                queryKey,
                userId,
                status,
                keyword
            )
        ).flow.map { pagingData ->
            pagingData.map { order ->
                Log.e("search", "searchOrder: $order")
                OrderUiModel.OrderItemUser(
                    OrderUiModel.OrderOfShop( //tạo sẵn orderitem có hết thông tin của order với shop, vì sau khi map ở đây rồi thì sẽ bị mất info nếu ko lưu lại
                        order.orderPojo.shopId,
                        order.orderPojo.shopGhnId,
                        order.orderPojo.shopName,
                        order.orderPojo.shopAvt
                    ),
                    OrderUiModel.Order(
                        order.orderPojo.orderId,
                        order.orderPojo.ghnOrderId,
                        order.orderPojo.orderStatus,
                    ),
                    com.example.shoppingapp.data.local.mapper.OrderMapper.mapToOrderItemDetail(
                        order.orderItemPojo,
                        order.productDetailPojo
                    )
                )
            }.insertSeparators { before, after -> //logic them cai phan cach giua cac item
                //add shop
                if (after == null) return@insertSeparators null
                val beforeId = before?.orderOfShop?.shopId
                val afterId = after.orderOfShop.shopId
                if (beforeId != afterId) {
                    OrderUiModel.OrderOfShop( //copy lại
                        after.orderOfShop.shopId,
                        after.orderOfShop.shopGhnId,
                        after.orderOfShop.shopName,
                        after.orderOfShop.shopAvt
                    )
                } else null
            }.insertSeparators { before, after ->
                //add order
                if (after == null) return@insertSeparators null
                if (before != null &&
                    before is OrderUiModel.OrderOfShop &&
                    after is OrderUiModel.OrderItemUser
                ) {
                    return@insertSeparators OrderUiModel.Order(
                        after.order.orderId,
                        after.order.ghnOrderId,
                        after.order.orderStatus
                    )
                }
                if (before != null &&
                    before is OrderUiModel.OrderItemUser &&
                    after is OrderUiModel.OrderItemUser &&
                    before.order.orderId != after.order.orderId
                ) {
                    return@insertSeparators OrderUiModel.Order(
                        after.order.orderId,
                        after.order.ghnOrderId,
                        after.order.orderStatus
                    )
                }
                null
            }.insertSeparators { before, after ->
                //add nút review
                if (after == null &&
                    before != null &&
                    before is OrderUiModel.OrderItemUser
                ) return@insertSeparators OrderUiModel.OrderReviewOrCancel(
                    before.orderOfShop.shopId,
                    before.orderOfShop.shopGhnId,
                    before
                )
                if (after != null &&
                    before != null &&
                    before is OrderUiModel.OrderItemUser &&
                    after is OrderUiModel.OrderOfShop
                ) {
                    OrderUiModel.OrderReviewOrCancel(
                        before.orderOfShop.shopId,
                        before.orderOfShop.shopGhnId,
                        before
                    )
                } else if (
                    after != null &&
                    before != null &&
                    before is OrderUiModel.OrderItemUser &&
                    after is OrderUiModel.Order
                ) {
                    OrderUiModel.OrderReviewOrCancel(
                        before.orderOfShop.shopId,
                        before.orderOfShop.shopGhnId,
                        before
                    )
                } else null
            }
        }
    }

    override suspend fun updateStatusOrderWhenConfirm(
        orderId: String,
        status: String
    ): StateListener<OrderUpdateStatus> = try {
        NetworkHelper.callApi(
            apiCall = {
                orderAPI.updateStatusOrderWhenConfirm(orderId, status)
            },
            mapper = { orderStatusDTO ->
                OrderMapper.mapToOrderUpdateStatus(orderStatusDTO)
            },
            moshi = moshi
        )
    } catch (e: Exception) {
        StateListener.Error(e)
    }
}