package com.example.shoppingapp.data.local.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shoppingapp.data.local.entity.CartEntity
import com.example.shoppingapp.data.local.entity.CartItemEntity

data class CartItemInCart(
    @Embedded val cartEntity: CartEntity,
    @Relation(
        parentColumn = "cartId", //cột khóa chính của bảng cartEntity
        entityColumn = "cartId", //cột khóa ngoại của bảng cartItemEntity
    )
    val cartItems: List<CartItemEntity>
)