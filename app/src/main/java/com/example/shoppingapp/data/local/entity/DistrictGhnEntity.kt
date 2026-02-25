package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "districts_ghn",
    foreignKeys = [
        ForeignKey(
            entity = ProvinceGhnEntity::class,
            parentColumns = ["provinceID"],
            childColumns = ["provinceID"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index("provinceID")
    ]
)
data class DistrictGhnEntity(
    @PrimaryKey val districtID: Int,
    val provinceID: Int,
    val districtName: String
)
