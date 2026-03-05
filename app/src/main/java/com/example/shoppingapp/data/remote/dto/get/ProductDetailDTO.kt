package com.example.shoppingapp.data.remote.dto.get

data class ProductDetailDTO(
    var maSp: Int? = null,
    var nguoiBanDTO: SellerDTO? = null,
    var giaSp: Int? = 0,
    var motaSp: String? = "",
    var giamgia: Int? = 0,
    var numberBought: Int? = 0,
    var listAnhMoTa: List<String>? = emptyList(),
    var totalProduct: Int? = 0,
    var rating: Int? = 0
)
