package com.example.shoppingapp.domain.model

data class StatusOrder(
    val statusId: Int,
    val statusName: String,
    var isSelected: Boolean
)
