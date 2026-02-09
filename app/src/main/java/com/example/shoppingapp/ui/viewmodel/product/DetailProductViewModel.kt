package com.example.shoppingapp.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.model.product.ProductDetail
import com.example.shoppingapp.domain.usecase.productusecase.get.GetOtherProductsOfShopUseCase
import com.example.shoppingapp.domain.usecase.productusecase.get.GetProductDetailUseCase
import com.example.shoppingapp.domain.usecase.productusecase.get.GetSanPhamRelatedUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailProductViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val getOtherProductsOfShopUseCase: GetOtherProductsOfShopUseCase,
    private val getSanPhamRelatedUseCase: GetSanPhamRelatedUseCase
) :
    ViewModel() {

    private val _productDetailStateFlow =
        MutableStateFlow<StateListener<ProductDetail>>(StateListener.Idle)
    val productDetailStateFLow = _productDetailStateFlow.asStateFlow()

    fun getProductDetail(id: Int) {
        viewModelScope.launch {
            getProductDetailUseCase(id).collectLatest {
                _productDetailStateFlow.value = it
            }
        }
    }

    fun getOtherProductsOfShop(id: Int): Flow<PagingData<ProductDisplay>> =
        getOtherProductsOfShopUseCase(id).cachedIn(viewModelScope)

    fun getSanPhamRelated(id: Int): Flow<PagingData<ProductDisplay>> =
        getSanPhamRelatedUseCase(id).cachedIn(viewModelScope)

}