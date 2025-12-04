package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "carts")
data class CartEntity (
    @PrimaryKey var cartId: Int,
    var maND: String,
    var tenND: String,
    var maNB: Int,
    var tenShop: String,
    var updateTime: LocalDateTime
)