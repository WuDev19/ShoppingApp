package com.example.shoppingapp.data.remote.dto

data class ListProductDTO(
    var offset: Int? = null,
    var size: Int? = null,
    var contents: List<ProductDTO>? = emptyList()
)
