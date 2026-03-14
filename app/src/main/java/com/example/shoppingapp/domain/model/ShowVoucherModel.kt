package com.example.shoppingapp.domain.model

data class ShowVoucherModel(
    val voucherId: Int,
    val voucherValue: String,
    val shopGhnId: Int,
    val voucherOriginValue: Int,
    val maxDiscount: Int,
    val voucherType: String
)
