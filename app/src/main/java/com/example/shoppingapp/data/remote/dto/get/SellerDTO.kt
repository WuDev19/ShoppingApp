package com.example.shoppingapp.data.remote.dto.get
data class SellerDTO(
    val maNguoiBan: Int? = 0,
    val maND: String? = "",
    val tenShop: String? = "",
    val sdtNguoiBan: String? = "",
    val avtShop: String? = "",
    val diaChi: String? = null,
    val sanphams: Set<ProductDTO>? = emptySet()
)
