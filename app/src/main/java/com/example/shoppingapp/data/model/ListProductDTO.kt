package com.example.shoppingapp.data.model

data class ListProductDTO(
    var page: Int? = null,
    var size: Int? = null,
    var contents: List<ProductDTO>? = emptyList()
)
