package com.example.shoppingapp.data.remote.dto.request.ghn

data class EstimateTimeGhnRequest(
    val to_district_id: Int,
    val to_ward_code: String,
    val service_id: Int
)
