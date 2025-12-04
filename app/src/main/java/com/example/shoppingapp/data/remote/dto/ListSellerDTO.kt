package com.example.shoppingapp.data.remote.dto

data class ListSellerDTO(
    var pageNumber: Int? = null,
    var pageSize: Int? = null,
    var contents: List<SellerDTO>? = emptyList()
)
