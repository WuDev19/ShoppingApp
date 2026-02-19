package com.example.shoppingapp.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.Cart
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.domain.model.CartUiModel
import com.example.shoppingapp.domain.usecase.cartusecase.GetCartItemInCartUseCase
import com.example.shoppingapp.domain.usecase.cartusecase.GetCartUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUserUseCase: GetCartUserUseCase,
    private val getCartItemInCartUseCase: GetCartItemInCartUseCase
) : ViewModel() {

    fun getCartUser(id: String): Flow<PagingData<CartUiModel>> =
        getCartUserUseCase(id).cachedIn(viewModelScope)

    fun getCartItemInCart(idCart: Int): Flow<PagingData<CartItem>> =
        getCartItemInCartUseCase(idCart).cachedIn(viewModelScope)

}