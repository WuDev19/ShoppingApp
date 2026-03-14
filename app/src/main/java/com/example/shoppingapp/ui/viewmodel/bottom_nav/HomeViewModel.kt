package com.example.shoppingapp.ui.viewmodel.bottom_nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.usecase.auth_usecase.GetUserCurrentUseCase
import com.example.shoppingapp.domain.usecase.cart_usecase.GetNumberCartItemUseCase
import com.example.shoppingapp.domain.usecase.product_usecase.get.GetProductUseCase
import com.example.shoppingapp.domain.usecase.product_usecase.get.SearchProductUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserCurrentUseCase,
    private val getProductsUseCase: GetProductUseCase,
    private val searchProductUseCase: SearchProductUseCase,
    private val getNumberCartItemUseCase: GetNumberCartItemUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _stateUserName = MutableStateFlow<StateListener<String>>(StateListener.Idle)
    val stateUserName = _stateUserName.asStateFlow()

    private val _numberCartState = MutableStateFlow<StateListener<Int>>(StateListener.Idle)
    val numberCartState = _numberCartState.asStateFlow()

    private val _searchProduct = MutableStateFlow("")
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchProduct = _searchProduct.debounce(500).flatMapLatest {
        searchProductUseCase(it)
    }.cachedIn(viewModelScope)

    fun getUserName() {
        viewModelScope.launch {
            getUserUseCase().collect {
                _stateUserName.value = it
            }
        }
    }

    fun getProduct(): Flow<PagingData<ProductDisplay>> = getProductsUseCase().cachedIn(viewModelScope)

    fun setSearchProductStateFlow(text: String){
        _searchProduct.value = text
    }

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun getNumberCartItem(){
        viewModelScope.launch {
            getNumberCartItemUseCase(auth.currentUser?.uid ?: "").collectLatest {
                _numberCartState.value = it
            }
        }
    }

}