package com.example.shoppingapp.domain.model

data class ShopVouchers(
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
    val endTime: String,
    val active: Boolean,
    var isSelected: Boolean
)
