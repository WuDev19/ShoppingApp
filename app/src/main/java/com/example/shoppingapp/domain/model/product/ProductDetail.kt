package com.example.shoppingapp.domain.model.product

data class ProductDetail(
    override var maSp: Int? = null,
    override var tenNguoiBan: String? = "",
    override var maNguoiBan: Int? = null,
    var avtShop: String? = "",
    val provinceId: Int? = null,
    val districtId: Int? = null,
    val wardCode: String? = null,
    val shopGhnId: Int? = null,
    val shopAddress: String? = null,
    override var giaSp: Int? = 0,
    override var motaSp: String? = "",
    override var giamgia: Int? = 0,
    override var numberBought: Int? = 0,
    var listAnhMoTa: List<String>? = emptyList(),
    var totalProduct: Int? = 0,
) : BaseProduct(
    maSp = maSp,
    tenNguoiBan = tenNguoiBan,
    maNguoiBan = maNguoiBan,
    giaSp = giaSp,
    numberBought = numberBought,
    motaSp = motaSp,
    giamgia = giamgia,
)
