package com.example.shoppingapp.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.remote.dto.request.CartAndCartItemRequest
import com.example.shoppingapp.data.remote.dto.request.CartItemRequest
import com.example.shoppingapp.data.remote.dto.request.CartRequest
import com.example.shoppingapp.domain.model.product.ProductChiTiet
import com.example.shoppingapp.domain.usecase.cartusecase.CreateCartUseCase
import com.example.shoppingapp.domain.usecase.productusecase.get.GetProductChiTietUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddToCartViewModel @Inject constructor(
    private val getProductChiTietUseCase: GetProductChiTietUseCase,
    private val createCartUseCase: CreateCartUseCase,
    private val auth: FirebaseAuth
) :
    ViewModel() {

    private val _sanPhamChiTietState =
        MutableStateFlow<StateListener<List<ProductChiTiet>>>(StateListener.Idle)
    val sanPhamChiTietState = _sanPhamChiTietState.asStateFlow()

    private val _createCart = MutableStateFlow<StateListener<Any>>(StateListener.Idle)
    val createCart = _createCart.asStateFlow()

    val selectedColor = MutableStateFlow("")
    val selectedSize = MutableStateFlow("")

    val displayCorrespondingProduct = combine(selectedSize, selectedColor) { size, color ->
        buildString {
            append(size)
            append("_")
            append(color)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun getSanPhamChiTiet(id: Int) {
        viewModelScope.launch {
            getProductChiTietUseCase(id).collectLatest {
                _sanPhamChiTietState.value = it
            }
        }
    }

    fun createCart(sellerId: Int, productDetailId: Int, number: Int, discount: Int) {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { currentId ->
                val cartRequest = CartRequest(sellerId, currentId)
                val cartItemRequest = CartItemRequest(productDetailId, number, discount)
                val cartAndCartItemRequest = CartAndCartItemRequest(cartRequest, cartItemRequest)
                createCartUseCase(cartAndCartItemRequest).collectLatest {
                    _createCart.value = it
                }
            }
        }
    }

}