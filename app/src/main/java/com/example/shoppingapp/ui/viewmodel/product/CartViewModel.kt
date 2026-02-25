package com.example.shoppingapp.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shoppingapp.domain.model.CartUiModel
import com.example.shoppingapp.domain.usecase.cartusecase.GetCartUserUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUserUseCase: GetCartUserUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    fun getCartUser(): Flow<PagingData<CartUiModel>> =
        getCartUserUseCase(auth.currentUser?.uid ?: "").cachedIn(viewModelScope)

}