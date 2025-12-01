package com.example.shoppingapp.data.remote.dto

data class ListCategoryAndProductDTO (
    var offset: Int? = null,
    var size: Int? = null,
    var contents: List<CategoryAndProductDTO>? = emptyList()
)
