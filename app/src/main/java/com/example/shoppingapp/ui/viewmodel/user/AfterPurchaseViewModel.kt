package com.example.shoppingapp.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.OrderStatus
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.GetOrderStatusUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AfterPurchaseViewModel @Inject constructor(
    private val getOrderStatusUseCase: GetOrderStatusUseCase
) : ViewModel() {

    private val _orderStatusState = Channel<StateListener<OrderStatus>>()
    val orderStatusState = _orderStatusState.receiveAsFlow()

    fun getOrderStatus(orderId: String) {
        viewModelScope.launch {
            _orderStatusState.send(StateListener.Loading)
            _orderStatusState.send(getOrderStatusUseCase(orderId))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getOrderStatus(orderIds: List<String>) {
        viewModelScope.launch {
            _orderStatusState.send(StateListener.Loading)
            orderIds.asFlow()
                .flatMapMerge(concurrency = 5) { orderId ->
                    flow {
                        emit(getOrderStatusUseCase(orderId))
                    }
                }
                .collect {
                    _orderStatusState.send(it)
                }
        }
    }

}