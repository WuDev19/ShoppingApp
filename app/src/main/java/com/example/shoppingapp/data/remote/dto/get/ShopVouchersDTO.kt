package com.example.shoppingapp.data.remote.dto.get

data class ShopVouchersDTO(
    val voucherId: Int,
    val voucherCode: String,
    val voucherDescription: String?,
    val voucherType: String,
    val sellerId: Int,
    val shopGhnId: Int,
    val minOrderCost: Int,
    val voucherValue: Int,
    val maxDiscountValue: Int,
    val usageLimit: Int,
    val usageCount: Int,
    val startTime: Long,
    val endTime: Long,
    val active: Boolean
)
