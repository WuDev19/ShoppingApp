package com.example.shoppingapp.data.remote.dto.get

data class OrderDetailDTO(
    val orderId: String,
    val orderStatus: String,
    val shippingMethod: String,
    val paymentMethod: String,
    val userName: String,
    val userPhone: String? = "",
    val address: String,
    val shopName: String,
    val shopAvt: String,
    val totalAmount: Int,
    val ngayDat: String,
    val ngayDuKienToi: String,
    val ghnOrderId: String,
    val userNote: String,
    val orderItems: List<OrderItemDetailDTO>
)
