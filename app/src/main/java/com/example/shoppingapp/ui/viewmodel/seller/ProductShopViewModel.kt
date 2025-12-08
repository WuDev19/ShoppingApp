package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.usecase.sellerusecase.GetAllProductOfShopUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProductShopViewModel @Inject constructor(private val getAllProductOfShopUseCase: GetAllProductOfShopUseCase) :
    ViewModel() {

    fun getAllProductOfShop(id: Int): Flow<PagingData<ProductDisplay>> =
        getAllProductOfShopUseCase(id).cachedIn(viewModelScope)

}