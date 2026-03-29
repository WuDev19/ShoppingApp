package com.example.shoppingapp.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.CancelOrder
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.CancelOrderUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.GetOrderViaStatusUseCase
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

@HiltViewModel
class MyOrderViewModel @Inject constructor(
    private val getOrderViaStatusUseCase: GetOrderViaStatusUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase
) : ViewModel() {
    private val _cancelOrderState = Channel<StateListener<CancelOrder>>()
    val cancelOrderState = _cancelOrderState.receiveAsFlow()
    private val _orderStatusState = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val orderStatusState = _orderStatusState
        .filterNotNull()
        .flatMapLatest { status ->
            getOrderViaStatusUseCase(status)
        }.cachedIn(viewModelScope)

    fun getOrderViaStatus(status: String) {
        _orderStatusState.value = status
    }

    fun cancelOrder(orderId: String, shopGhnId: Int, orderGhnId: String) {
        viewModelScope.launch {
            _cancelOrderState.send(StateListener.Loading)
            val result = cancelOrderUseCase(orderId, shopGhnId, orderGhnId)
            _cancelOrderState.send(result)
        }
    }

}