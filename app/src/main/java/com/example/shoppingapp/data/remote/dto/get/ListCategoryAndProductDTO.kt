package com.example.shoppingapp.data.remote.dto.get

data class ListCategoryAndProductDTO (
    var pageNumber: Int? = null,
    var pageSize: Int? = null,
    var contents: List<CategoryAndProductDTO>? = emptyList()
)
