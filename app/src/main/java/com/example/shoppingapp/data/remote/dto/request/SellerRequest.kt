package com.example.shoppingapp.data.remote.dto.request

data class SellerRequest(
    val maND: String,
    val tenShop: String,
    val sdtNguoiBan: String,
    val tenDiaChi: String,
    val provinceId: Int,
    val districtId: Int,
    val wardCode: String,
    var avtShop: String,
    val roleName: String
)
