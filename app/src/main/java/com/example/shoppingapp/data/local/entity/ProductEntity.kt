package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        Index("maNguoiBan", "motaSp"),
        Index("maNguoiBan", "numberBought")
    ]
)
data class ProductEntity(
    @PrimaryKey val id: Int,
    val tenNguoiBan: String,
    val giaSp: Int,
    val motaSp: String,
    val thumbnail: String,
    val giamgia: Int,
    val numberBought: Int,
    val maNguoiBan: Int
)
