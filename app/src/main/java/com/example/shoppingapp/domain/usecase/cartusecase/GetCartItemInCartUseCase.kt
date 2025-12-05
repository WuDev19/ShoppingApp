package com.example.shoppingapp.domain.usecase.cartusecase

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.domain.repository.cart.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartItemInCartUseCase @Inject constructor(private val repo: CartRepository) {

    operator fun invoke(idCart: Int): Flow<PagingData<CartItem>> = repo.getCartItemInCart(idCart)

}