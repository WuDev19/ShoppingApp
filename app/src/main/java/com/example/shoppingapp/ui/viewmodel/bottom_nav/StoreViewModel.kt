package com.example.shoppingapp.ui.viewmodel.bottom_nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.usecase.productusecase.GetProductByCategoryUseCase
import com.example.shoppingapp.domain.usecase.productusecase.GetProductUseCase
import com.example.shoppingapp.domain.usecase.productusecase.SearchProductUseCase
import com.example.shoppingapp.domain.usecase.sellerusecase.GetAllSellerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val productUseCase: GetProductUseCase,
    private val productByCategoryUseCase: GetProductByCategoryUseCase,
    private val getAllSellerUseCase: GetAllSellerUseCase,
    private val searchProductUseCase: SearchProductUseCase,
) : ViewModel() {

    private val _searchStateFlow = MutableStateFlow("")
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchFlow = _searchStateFlow.debounce(500).flatMapLatest {
        searchProductUseCase(it)
    }.cachedIn(viewModelScope)

    fun getProduct(): Flow<PagingData<Product>> = productUseCase().cachedIn(viewModelScope)

    fun getProductByCategory(theLoai: String): Flow<PagingData<Product>> =
        productByCategoryUseCase(theLoai).cachedIn(viewModelScope)

    fun getAllSeller(): Flow<PagingData<SellerInStore>> = getAllSellerUseCase().cachedIn(viewModelScope)

    fun setSearchStateFlow(text: String){
        _searchStateFlow.value = text
    }

}