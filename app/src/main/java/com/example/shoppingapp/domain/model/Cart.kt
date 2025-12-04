package com.example.shoppingapp.domain.model

import java.time.LocalDateTime

data class Cart (
    var cartId: Int? = 0,
    var maND: String? = "",
    var tenND: String? = "",
    var maNB: Int? = 0,
    var tenShop: String? = "",
    var updateTime: LocalDateTime? = null
)