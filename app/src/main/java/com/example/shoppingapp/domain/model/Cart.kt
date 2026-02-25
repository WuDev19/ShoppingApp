package com.example.shoppingapp.domain.model

data class Cart(
    var cartId: Int? = 0,
    var maND: String? = "",
    var tenND: String? = "",
    var maNB: Int? = 0,
    var tenShop: String? = "",
    var avtShop: String? = "",
    var updateTime: Long? = 0,
) : CartUiModel()