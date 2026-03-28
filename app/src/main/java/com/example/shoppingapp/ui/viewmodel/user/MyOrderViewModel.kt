package com.example.shoppingapp.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.GetOrderViaStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class MyOrderViewModel @Inject constructor(
    private val getOrderViaStatusUseCase: GetOrderViaStatusUseCase
) : ViewModel() {

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

}