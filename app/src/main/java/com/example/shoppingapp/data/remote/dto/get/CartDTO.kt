package com.example.shoppingapp.data.remote.dto.get

data class CartDTO(
    var cartId: Int? = 0,
    var maND: String? = "",
    var tenND: String? = "",
    var maNB: Int? = 0,
    var tenShop: String? = "",
    var avtShop: String? = "",
    val provinceIdShop: Int? = null,
    val districtIdShop: Int? = null,
    val wardCodeShop: String? = null,
    val shopGhnId: Int? = null,
    var updateTime: Long? = null,
    val cartItemDTOS: List<CartItemDTO> = emptyList()
)
