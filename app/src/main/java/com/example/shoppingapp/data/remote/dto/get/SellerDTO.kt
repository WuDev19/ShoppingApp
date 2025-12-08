package com.example.shoppingapp.data.remote.dto.get

data class SellerDTO(
    var maNguoiBan: Int? = 0,
    var tenShop: String? = "",
    var sdtNguoiBan: String? = "",
    var avtShop: String? = "",
    var diachi: DiaChiDTO? = null,
    var sanphams: Set<ProductDTO>? = emptySet()
)
