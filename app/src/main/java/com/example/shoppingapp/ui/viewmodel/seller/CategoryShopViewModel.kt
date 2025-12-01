package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.CategoryAndQuantity
import com.example.shoppingapp.domain.usecase.sellerusecase.CategoryProductOfShopUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryShopViewModel @Inject constructor(private val categoryProductOfShopUseCase: CategoryProductOfShopUseCase) :
    ViewModel() {

    private val _stateCategoryAndQuantity =
        MutableStateFlow<StateListener<List<CategoryAndQuantity>>>(StateListener.Idle)
    val stateCateAndQuantity = _stateCategoryAndQuantity.asStateFlow()

    fun getDanhMucVaSl(id: Int) {
        viewModelScope.launch {
            categoryProductOfShopUseCase(id).collectLatest {
                _stateCategoryAndQuantity.value = it
            }
        }
    }

}