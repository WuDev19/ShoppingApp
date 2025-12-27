package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_address")
data class UserAddressEntity (
    @PrimaryKey val idAddress: Int,
    val name: String,
    val sdt: String,
    val addressName: String,
    val defaultAddress: Boolean,
    val maND: String
)