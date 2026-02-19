package com.example.shoppingapp.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shoppingapp.data.local.entity.CartEntity
import com.example.shoppingapp.data.local.entity.CartItemEntity

data class CartItemInCart(
    @Embedded val cartEntity: CartEntity,
    @Relation(
        parentColumn = "cartId",
        entityColumn = "cartId",
    )
    val cartItems: List<CartItemEntity>
)