package com.example.shoppingapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    var cartId: Int,
    var maND: String,
    var tenND: String,
    var maNB: Int,
    var tenShop: String,
    var avtShop: String,
    var provinceIdShop: Int,
    var districtIdShop: Int,
    var wardCodeShop: String,
    var shopGhnId: Int,
    var updateTime: Long,
) : CartUiModel(), Parcelable