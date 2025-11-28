package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_key_remotes")
data class RemoteKeyProduct(
    @PrimaryKey val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
