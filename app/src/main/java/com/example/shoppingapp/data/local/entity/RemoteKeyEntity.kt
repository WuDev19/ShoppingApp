package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_key")
data class RemoteKeyEntity(
    @PrimaryKey val queryKey: String,
    val prevKey: Int?,
    val nextKey: Int?
)
