package com.example.shoppingapp.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.OrderDetail
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.GetOrderDetailUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val getOrderDetailUseCase: GetOrderDetailUseCase
) : ViewModel() {

    private val _orderDetailState = Channel<StateListener<OrderDetail>>()
    val orderDetailState = _orderDetailState.receiveAsFlow()

    fun getOrderDetail(orderId: Int){
        viewModelScope.launch {
            _orderDetailState.send(StateListener.Loading)
            val result = getOrderDetailUseCase(orderId)
            _orderDetailState.send(result)
        }
    }
}