package com.example.shoppingapp.domain.repository.cart

import androidx.paging.PagingData
import com.example.shoppingapp.data.remote.dto.post.CartAndCartItemRequest
import com.example.shoppingapp.domain.model.Cart
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun createCart(cartAndCartItemRequest: CartAndCartItemRequest): Flow<StateListener<Any>>

    fun getCartUser(idUser: String): Flow<PagingData<Cart>>

    fun getCartItemInCart(idCart: Int): Flow<PagingData<CartItem>>

    fun getNumberOfCartItem(id: String): Flow<StateListener<Int>>

}