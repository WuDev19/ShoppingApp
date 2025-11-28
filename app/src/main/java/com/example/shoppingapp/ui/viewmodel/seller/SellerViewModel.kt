package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.usecase.sellerusecase.GetSellerDetailInfoUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(private val getSellerDetailInfoUseCase: GetSellerDetailInfoUseCase): ViewModel() {

    private val _sellerDetailState = MutableStateFlow<StateListener<SellerInStore>>(StateListener.Idle)
    val sellerDetailState = _sellerDetailState.asStateFlow()

    private val _searchProductShop = MutableStateFlow("")
    val searchProductShop = _searchProductShop.debounce(300).flatMapLatest {

    }

    fun getSellerDetail(id: Int) {
        viewModelScope.launch {
            getSellerDetailInfoUseCase(id).collectLatest {
                _sellerDetailState.value = it
            }
        }
    }

    fun setSearchProductShop(text: String){
        _searchProductShop.value = text
    }

}