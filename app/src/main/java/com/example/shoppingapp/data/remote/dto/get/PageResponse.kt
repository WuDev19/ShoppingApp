package com.example.shoppingapp.data.remote.dto.get

data class PageResponse <T>(
    val pageNumber: Int,
    val pageSize: Int,
    val contents: List<T>? = emptyList()
)
