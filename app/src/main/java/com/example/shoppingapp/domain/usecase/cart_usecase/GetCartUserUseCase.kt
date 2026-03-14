package com.example.shoppingapp.domain.usecase.cart_usecase

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.CartUiModel
import com.example.shoppingapp.domain.repository.cart.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartUserUseCase @Inject constructor(private val repo: CartRepository){

    operator fun invoke(id: String): Flow<PagingData<CartUiModel>> = repo.getCartUser(id)

}