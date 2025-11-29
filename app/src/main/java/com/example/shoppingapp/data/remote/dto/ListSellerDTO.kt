package com.example.shoppingapp.data.remote.dto

data class ListSellerDTO(
    var offset: Int? = 0,
    var size: Int? = 0,
    var contents: List<SellerDTO>? = emptyList()
)
