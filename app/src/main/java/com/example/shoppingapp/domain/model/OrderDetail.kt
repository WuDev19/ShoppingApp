package com.example.shoppingapp.domain.model

data class OrderDetail(
    val orderId: String,
    val orderStatus: String,
    val shippingMethod: String,
    val paymentMethod: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val address: String,
    val shopId: Int,
    val shopGhnId: Int,
    val shopName: String,
    val shopAvt: String,
    val totalAmount: Int,
    val ngayDat: String,
    val ngayDuKienToi: String,
    val ngayHoanThanh: String,
    val ghnOrderId: String,
    val userNote: String,
    val orderItems: List<OrderItemDetail>
)
