package com.example.shoppingapp.domain.usecase.cartusecase

import com.example.shoppingapp.data.remote.dto.request.CartAndCartItemRequest
import com.example.shoppingapp.domain.repository.cart.CartRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateCartUseCase @Inject constructor(private val repo: CartRepository) {

    operator fun invoke(cartAndCartItemRequest: CartAndCartItemRequest): Flow<StateListener<Any>> =
        repo.createCart(cartAndCartItemRequest)

}