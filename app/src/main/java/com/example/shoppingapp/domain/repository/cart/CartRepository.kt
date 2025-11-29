package com.example.shoppingapp.domain.repository.cart

import com.example.shoppingapp.data.remote.dto.CartAndCartItemRequest
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun createCart(cartAndCartItemRequest: CartAndCartItemRequest): Flow<StateListener<Any>>

}