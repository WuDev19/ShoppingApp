package com.example.shoppingapp.domain.usecase.cartusecase

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.Cart
import com.example.shoppingapp.domain.repository.cart.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartUserUseCase @Inject constructor(private val repo: CartRepository){

    operator fun invoke(id: String): Flow<PagingData<Cart>> = repo.getCartUser(id)

}