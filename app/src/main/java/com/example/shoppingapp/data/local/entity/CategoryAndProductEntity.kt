package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_product_shop")
data class CategoryAndProductEntity(
    @PrimaryKey val id: Int,
    val tenNguoiBan: String,
    val giaSp: Int,
    val motaSp: String,
    val thumbnail: String,
    val giamgia: Int,
    val numberBought: Int,
    val maNguoiBan: Int,
    val tenDanhMuc: String
)
