package com.example.shoppingapp.data.remote.dto

import java.time.LocalDateTime

data class CartDTO(
    var cartId: Int? = 0,
    var maND: String? = "",
    var tenND: String? = "",
    var maNB: Int? = 0,
    var tenShop: String? = "",
    var updateTime: LocalDateTime? = null
)
