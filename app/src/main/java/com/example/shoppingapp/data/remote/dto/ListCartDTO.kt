package com.example.shoppingapp.data.remote.dto

data class ListCartDTO (
    var pageNumber: Int? = null,
    var pageSize: Int? = null,
    var contents: List<CartDTO>? = emptyList()
)