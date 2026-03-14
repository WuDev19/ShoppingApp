package com.example.shoppingapp.domain.model

data class CreateOrderGhn(
    val shop_id: Int,//
    val to_name: String,//
    val to_phone: String,//
    val to_address: String,//
    val to_ward_code: String,//
    val to_district_id: Int,//
    val weight: Int,//
    val service_id: Int,//
    val service_type_id: Int,//
    val payment_type_id: Int,//
    val insurance_value: Int,//
    val cod_amount: Int,//
    val note: String,//
    val required_note: String,//
    val items: List<CreateOrderItemGhn>//
)