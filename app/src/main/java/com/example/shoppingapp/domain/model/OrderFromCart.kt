package com.example.shoppingapp.domain.model

class OrderFromCart {
    lateinit var cart: Cart
    lateinit var cartItems: List<CartItem>

    override fun toString(): String {
        return cart.toString() + "\n" + cartItems
    }
}
