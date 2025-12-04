package com.example.shoppingapp.data.remote.dto

data class ListProductDTO(
    var pageNumber: Int? = null,
    var pageSize: Int? = null,
    var contents: List<ProductDTO>? = emptyList()
)
