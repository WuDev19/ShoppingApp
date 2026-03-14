package com.example.shoppingapp.domain.model

data class FinalPrice (
    val finalPrice: Long,
    val shopGhnId: Int,
    val totalPriceOrigin: Long,
    val discountValue: Int,
    val priceWithoutShip: Long
)