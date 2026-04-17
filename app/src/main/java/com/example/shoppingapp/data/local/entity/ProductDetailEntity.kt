package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_details",
    indices = [
        Index("tenDetail", name = "index_product_details_tenDetail")
    ]
)
data class ProductDetailEntity(
    @PrimaryKey val maSanPhamDeTail: Int,
    val tenDetail: String,
    val inStock: Int,
    val anhDetail: String,
    val unitPrice: Int,
    val color: String,
    val kichco: String,
    val width: Int,
    val height: Int,
    val length: Int,
    val weight: Int,
    val maSp: Int
)
