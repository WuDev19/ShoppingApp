package com.example.shoppingapp.data.remote.dto.request.ghn

data class CreateOrderGhnRequest(
    val to_name: String,
    val to_phone: String,
    val to_address: String,
    val to_ward_code: String,
    val to_district_id: Int,
    val weight: Int,
    val service_id: Int,
    val service_type_id: Int,
    val payment_type_id: Int,
    val insurance_value: Long,
    val cod_amount: Long,
    val note: String,
    val required_note: String,
    val items: List<CreateOrderItemGhnRequest>
)
