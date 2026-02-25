package com.example.shoppingapp.data.remote.dto.get

data class ProductChiTietDTO(
    var maSanPhamDeTail: Int? = null,
    var tenDetail: String? = "",
    var inStock: Int? = 0,
    var anhDetail: String? = null,
    var unitPrice: Int? = 0,
    var color: String? = "",
    var kichco: String? = "",
    var width: Int? = 1,
    var height: Int? = 1,
    var length: Int? = 1,
    var weight: Int? = 1,
    var maSp: Int? = null
)
