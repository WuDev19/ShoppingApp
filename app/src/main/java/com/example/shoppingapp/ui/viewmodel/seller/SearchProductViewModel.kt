package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.SearchProductShop
import com.example.shoppingapp.domain.usecase.seller_usecase.SearchProductInShopUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchProductViewModel @Inject constructor(
    private val searchProductInShopUseCase: SearchProductInShopUseCase
) : ViewModel() {

    private val _searchProductShop = MutableStateFlow<SearchProductShop?>(null)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchProductShop = _searchProductShop.debounce(500).flatMapLatest {
        searchProductInShopUseCase(it?.id ?: -1, it?.text ?: "")
    }.cachedIn(viewModelScope)

    fun setSearchProductShop(searchProduct: SearchProductShop) {
        _searchProductShop.value = searchProduct
    }

}