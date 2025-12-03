package com.example.shoppingapp.ui.viewmodel.seller

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.SearchProductShop
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.usecase.sellerusecase.GetSellerDetailInfoUseCase
import com.example.shoppingapp.domain.usecase.sellerusecase.SearchProductInShopUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(
    private val getSellerDetailInfoUseCase: GetSellerDetailInfoUseCase,
    private val searchProductInShopUseCase: SearchProductInShopUseCase
) : ViewModel() {

    private val _sellerDetailState =
        MutableStateFlow<StateListener<SellerInStore>>(StateListener.Idle)
    val sellerDetailState = _sellerDetailState.asStateFlow()

    var positionTabState: Int = 0
    var fragment: Fragment? = null

    private val _searchProductShop = MutableStateFlow<SearchProductShop?>(null)
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchProductShop = _searchProductShop.debounce(500).flatMapLatest {
        searchProductInShopUseCase(it?.id ?: -1, it?.text ?: "")
    }.cachedIn(viewModelScope)


    fun getSellerDetail(id: Int) {
        viewModelScope.launch {
            getSellerDetailInfoUseCase(id).collectLatest {
                _sellerDetailState.value = it
            }
        }
    }

    fun setSearchProductShop(searchProduct: SearchProductShop) {
        _searchProductShop.value = searchProduct
    }

}