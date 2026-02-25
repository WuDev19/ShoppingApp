package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "wards_ghn",
    foreignKeys = [
        ForeignKey(
            entity = DistrictGhnEntity::class,
            parentColumns = ["districtID"],
            childColumns = ["districtID"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("districtID")]
)
data class WardGhnEntity(
    @PrimaryKey val wardCode: Int,
    val districtID: Int,
    val wardName: String
)
