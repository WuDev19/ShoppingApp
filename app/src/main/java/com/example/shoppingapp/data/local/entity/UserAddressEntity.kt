package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_address",
    indices = [
        Index(
            "maND", "defaultAddress",
            orders = [Index.Order.ASC, Index.Order.DESC]
        )
    ]
)
data class UserAddressEntity(
    @PrimaryKey val idAddress: Int,
    val name: String,
    val sdt: String,
    val addressName: String,
    val defaultAddress: Boolean,
    val maND: String
)