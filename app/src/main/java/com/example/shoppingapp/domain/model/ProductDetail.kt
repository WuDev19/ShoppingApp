package com.example.shoppingapp.domain.model

data class ProductDetail(
    var maSp: Int? = null,
    var tenNguoiBan: String? = "",
    var maNguoiBan: Int? = null,
    var avtShop: String? = "",
    var giaSp: Int? = 0,
    var motaSp: String? = "",
    var giamgia: Int? = 0,
    var numberBought: Int? = 0,
    var listAnhMoTa: List<String>? = emptyList(),
    var totalProduct: Int? = 0,
)
