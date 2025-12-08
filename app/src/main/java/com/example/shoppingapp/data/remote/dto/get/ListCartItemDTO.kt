package com.example.shoppingapp.data.remote.dto.get

data class ListCartItemDTO (
    val pageNumber: Int? = null,
    val pageSize: Int? = null,
    val contents: List<CartItemDTO>? = emptyList()
)