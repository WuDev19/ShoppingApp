package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    indices = [
        Index(
            "userId",
            "orderStatus",
            "ngayDuKienToi",
            orders = [Index.Order.ASC, Index.Order.ASC, Index.Order.DESC]
        )
    ]
)
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val orderStatus: String,
    val shippingMethod: String,
    val paymentMethod: String,
    val userId: String,
    val userName: String, //tên khi đặt hàng
    val userPhone: String, //sdt khi đặt hàng (có thể để khác với user đặt)
    val address: String,
    val shopId: Int,
    val shopGhnId: Int,
    val shopName: String,
    val shopAvt: String,
    val totalAmount: Int,
    val ngayDat: String,
    val ngayDuKienToi: String,
    val ngayHoanThanh: String,
    val ngayChinhSua: Long,
    val ghnOrderId: String,
    val userNote: String,
)
