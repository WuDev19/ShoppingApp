package com.example.shoppingapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class OrderFromCart: Parcelable {
    lateinit var cart: Cart
    lateinit var cartItems: List<CartItem>

    override fun toString(): String {
        return cart.toString() + "\n" + cartItems
    }
}
