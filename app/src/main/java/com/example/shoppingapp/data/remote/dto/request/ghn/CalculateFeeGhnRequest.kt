package com.example.shoppingapp.data.remote.dto.request.ghn

data class CalculateFeeGhnRequest(
    val to_ward_code: String,
    val to_district_id: Int,
    val weight: Int,
    var service_id: Int? = null,
    var service_type_id: Int? = null,
    val insurance_value: Int,
    val cod_value: Int
)