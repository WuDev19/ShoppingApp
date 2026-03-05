package com.example.shoppingapp.data.remote.dto.get.ghn

data class CalculateFeeGhnDTO(
    val total: Int,
    val service_fee: Int,
    val insurance_fee: Int,
    val cod_fee: Int
)
