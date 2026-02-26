package com.example.shoppingapp.data.remote.dto.request

data class UserAddressRequest(
    val tenDiaChi: String?,
    val provinceId: Int?,
    val districtId: Int?,
    val wardCode: String?,
    val diaChiMacDinh: Boolean?,
    val tenND: String?,
    val sdtND: String?,
    val maND: String?
)
