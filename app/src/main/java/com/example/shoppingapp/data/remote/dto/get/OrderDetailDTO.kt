package com.example.shoppingapp.data.remote.dto.get

data class OrderDetailDTO(
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
    val ngayChinhSua: Long,
    val ghnOrderId: String,
    val userNote: String,
    val orderItems: List<OrderItemDetailDTO>,
    val productIds: Set<Int>
)
