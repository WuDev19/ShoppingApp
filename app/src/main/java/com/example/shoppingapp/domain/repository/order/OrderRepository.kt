package com.example.shoppingapp.domain.repository.order

import androidx.paging.PagingData
import com.example.shoppingapp.data.remote.dto.request.CancelOrderRequest
import com.example.shoppingapp.data.remote.dto.request.OrderPaymentRequest
import com.example.shoppingapp.data.remote.dto.request.OrderRequest
import com.example.shoppingapp.domain.model.AfterPayment
import com.example.shoppingapp.domain.model.CancelOrder
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.domain.model.OrderDetail
import com.example.shoppingapp.domain.model.OrderStatus
import com.example.shoppingapp.domain.model.OrderUiModel
import com.example.shoppingapp.domain.model.user.OrderUpdateStatus
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun createOrder(shopGhnId: Int, orderRequest: OrderRequest): StateListener<Order>
    suspend fun createPayment(orderPaymentRequest: OrderPaymentRequest): StateListener<AfterPayment>
    suspend fun updateStatusOrderWhenPaymentSuccess(orderId: String): StateListener<Order>
    suspend fun updateStatusOrderWhenConfirm(
        orderId: String,
        status: String
    ): StateListener<OrderUpdateStatus>

    suspend fun getOrderStatus(orderId: String): StateListener<OrderStatus>
    suspend fun getOrderDetail(orderId: String): StateListener<OrderDetail>
    fun getOrderViaStatus(userId: String, status: String): Flow<PagingData<OrderUiModel>>
    suspend fun cancelOrder(
        orderId: String,
        cancelOrderRequest: CancelOrderRequest
    ): StateListener<CancelOrder>

    fun searchOrder(userId: String, status: String, keyword: String): Flow<PagingData<OrderUiModel>>
}