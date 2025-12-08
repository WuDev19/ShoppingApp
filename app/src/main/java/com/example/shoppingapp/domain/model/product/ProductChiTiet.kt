package com.example.shoppingapp.domain.model.product

data class ProductChiTiet (
    var maSanPhamDeTail: Int? = null,
    var tenDetail: String? = "",
    var inStock: Int? = 0,
    var anhDetail: String? = null,
    var unitPrice: Int? = 0,
    var color: String? = "",
    var kichco: String? = "",
    var maSp: Int? = null
)