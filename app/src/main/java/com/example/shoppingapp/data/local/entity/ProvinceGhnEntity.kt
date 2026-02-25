package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "provinces_ghn")
data class ProvinceGhnEntity(
    @PrimaryKey val provinceID: Int,
    val provinceName: String
)
