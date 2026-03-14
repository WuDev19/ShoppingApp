package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.ShopVouchers
import com.example.shoppingapp.domain.usecase.seller_usecase.GetShopVouchersUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoucherOfShopViewModel @Inject constructor(
    private val getShopVouchersUseCase: GetShopVouchersUseCase
) : ViewModel() {

    private val _shopVoucherState =
        MutableStateFlow<StateListener<List<ShopVouchers>>>(StateListener.Idle)
    val shopVoucherState = _shopVoucherState.asStateFlow()

    fun getShopVouchers(sellerId: Int) {
        viewModelScope.launch {
            _shopVoucherState.value = StateListener.Loading
            val result = getShopVouchersUseCase(sellerId)
            _shopVoucherState.value = result
        }
    }

}