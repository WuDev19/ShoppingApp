package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_item",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = ProductDetailEntity::class,
            parentColumns = ["maSanPhamDeTail"],
            childColumns = ["maSanPhamDeTail"]
        )
    ],
    indices = [
        Index("maSanPhamDeTail"),
        Index("orderId")
    ]
)
data class OrderItemEntity(
    @PrimaryKey val orderItemId: Int,
    val maSanPhamDeTail: Int,
    val number: Int,
    val price: Int,
    val orderId: String,
    val reviewId: Int?
)
