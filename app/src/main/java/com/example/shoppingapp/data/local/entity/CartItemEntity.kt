package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val cartItemId: Int,
    val maGioHang: Int,
    val maSanPhamDeTail: Int,
    val tenDetail: String,
    val inStock: Int,
    val anhDetail: String,
    val unitPrice: Int,
    val color: String,
    val kichco: String,
    val maSp: Int,
    val soLuong: Int,
    val giamGia: Int
)
