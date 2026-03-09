package com.example.shoppingapp.data.remote.dto.request

data class VoucherRequest(
    val voucherCode: String,
    val voucherDescription: String?,
    val voucherType: String,
    val minCost: Int,
    val usageLimit: Int,
    val discountValue: Int,
    val maxDiscount: Int,
    val startTime: Long,
    val endTime: Long
)
