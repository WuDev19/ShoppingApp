package com.example.shoppingapp.domain.usecase.cart_usecase

import com.example.shoppingapp.domain.repository.cart.CartRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNumberCartItemUseCase @Inject constructor(private val repo: CartRepository) {

    operator fun invoke(id: String): Flow<StateListener<Int>> = repo.getNumberOfCartItem(id)

}