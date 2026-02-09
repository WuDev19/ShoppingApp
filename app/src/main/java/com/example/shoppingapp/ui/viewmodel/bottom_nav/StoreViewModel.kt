package com.example.shoppingapp.ui.viewmodel.bottom_nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.usecase.cartusecase.GetNumberCartItemUseCase
import com.example.shoppingapp.domain.usecase.productusecase.get.GetProductByCategoryUseCase
import com.example.shoppingapp.domain.usecase.productusecase.get.GetProductUseCase
import com.example.shoppingapp.domain.usecase.productusecase.get.SearchProductUseCase
import com.example.shoppingapp.domain.usecase.sellerusecase.GetAllSellerUseCase
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
class StoreViewModel @Inject constructor(
    private val productUseCase: GetProductUseCase,
    private val productByCategoryUseCase: GetProductByCategoryUseCase,
    private val getAllSellerUseCase: GetAllSellerUseCase,
    private val searchProductUseCase: SearchProductUseCase,
    private val auth: FirebaseAuth,
    private val getNumberCartItemUseCase: GetNumberCartItemUseCase
) : ViewModel() {

    private val _numberCartState = MutableStateFlow<StateListener<Int>>(StateListener.Idle)
    val numberCartState = _numberCartState.asStateFlow()

    private val _searchStateFlow = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchFlow = _searchStateFlow.debounce(500).flatMapLatest {
        searchProductUseCase(it)
    }.cachedIn(viewModelScope)

    fun getProduct(): Flow<PagingData<ProductDisplay>> = productUseCase().cachedIn(viewModelScope)

    fun getProductByCategory(theLoai: String): Flow<PagingData<ProductDisplay>> =
        productByCategoryUseCase(theLoai).cachedIn(viewModelScope)

    fun getAllSeller(): Flow<PagingData<SellerInStore>> =
        getAllSellerUseCase().cachedIn(viewModelScope)

    fun setSearchStateFlow(text: String) {
        _searchStateFlow.value = text
    }

    fun getNumberCartItem() {
        viewModelScope.launch {
            getNumberCartItemUseCase(auth.currentUser?.uid ?: "").collectLatest {
                _numberCartState.value = it
            }
        }
    }

}