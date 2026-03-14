package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.usecase.seller_usecase.GetBestSellerByShopUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BestSellerViewModel @Inject constructor(private val getBestSellerByShopUseCase: GetBestSellerByShopUseCase) : ViewModel() {

    fun getBestSellerProductShop(id: Int): Flow<PagingData<ProductDisplay>> = getBestSellerByShopUseCase(id).cachedIn(viewModelScope)

}