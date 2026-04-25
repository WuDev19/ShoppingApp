package com.example.shoppingapp.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.CancelOrder
import com.example.shoppingapp.domain.model.OrderSearchOrNot
import com.example.shoppingapp.domain.model.user.OrderUpdateStatus
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.CancelOrderUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.GetOrderViaStatusUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.SearchOrderUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.UpdateStatusOrderWhenConfirm
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MyOrderViewModel @Inject constructor(
    private val getOrderViaStatusUseCase: GetOrderViaStatusUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val searchOrderUseCase: SearchOrderUseCase,
    private val updateStatusOrderWhenConfirm: UpdateStatusOrderWhenConfirm,
    @param:Named("userId") private val userId: String
) : ViewModel() {
    private val _cancelOrderState = Channel<StateListener<CancelOrder>>()
    val cancelOrderState = _cancelOrderState.receiveAsFlow()
    private val _updateConfirmStatus = Channel<StateListener<OrderUpdateStatus>>()
    val updateConfirmStatus = _updateConfirmStatus.receiveAsFlow()
    private val _orderStatusSearchOrNotState = MutableStateFlow<OrderSearchOrNot?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val orderStatusSearchOrNotState = _orderStatusSearchOrNotState
        .filterNotNull()
        .flatMapLatest { order ->
            if (order.keyword.isEmpty()) {
                getOrderViaStatusUseCase(order.status)
            } else {
                searchOrderUseCase(
                    userId,
                    order.status,
                    order.keyword
                )
            }
        }.cachedIn(viewModelScope)

    fun setOrderSearchOrNot(orderSearchOrNot: OrderSearchOrNot?) {
        _orderStatusSearchOrNotState.value = orderSearchOrNot
    }

    fun cancelOrder(orderId: String, shopGhnId: Int, orderGhnId: String) {
        viewModelScope.launch {
            _cancelOrderState.send(StateListener.Loading)
            val result = cancelOrderUseCase(orderId, shopGhnId, orderGhnId)
            _cancelOrderState.send(result)
        }
    }

    fun confirmReceivedOrder(orderId: String, status: String){
        viewModelScope.launch {
            _updateConfirmStatus.send(StateListener.Loading)
            val result = updateStatusOrderWhenConfirm(orderId, status)
            _updateConfirmStatus.send(result)
        }
    }
}