package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carts")
data class CartEntity (
    @PrimaryKey val cartId: Int,
    val maND: String,
    val tenND: String,
    val maNB: Int,
    val tenShop: String,
    val avtShop: String,
    val updateTime: Long
)