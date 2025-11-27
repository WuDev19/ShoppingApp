package com.example.shoppingapp.domain.repository.cart

import com.example.shoppingapp.data.model.CartAndCartItemRequest
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun createCart(cartAndCartItemRequest: CartAndCartItemRequest): Flow<StateListener<Any>>

}