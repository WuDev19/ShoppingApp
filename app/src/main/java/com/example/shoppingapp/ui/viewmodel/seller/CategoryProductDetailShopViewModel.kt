package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.domain.usecase.sellerusecase.CategoryProductDetailShopUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CategoryProductDetailShopViewModel @Inject constructor(private val categoryProductDetailShopUseCase: CategoryProductDetailShopUseCase): ViewModel() {

    fun getCategoryAndProductShop(id: Int, tenDanhMuc: String): Flow<PagingData<Product>> = categoryProductDetailShopUseCase(id, tenDanhMuc).cachedIn(viewModelScope)

}