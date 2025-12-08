package com.example.shoppingapp.domain.model.product

data class ProductDisplay(
    override var maSp: Int?,
    override var tenNguoiBan: String?,
    override var giaSp: Int?,
    override var motaSp: String?,
    var thumbnail: String?,
    override var giamgia: Int?,
    override var numberBought: Int?,
    override var maNguoiBan: Int?
) : BaseProduct(
    maSp,
    tenNguoiBan,
    giaSp,
    motaSp,
    giamgia,
    numberBought,
    maNguoiBan
)
