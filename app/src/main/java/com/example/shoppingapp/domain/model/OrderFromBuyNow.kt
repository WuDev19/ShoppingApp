package com.example.shoppingapp.domain.model

import android.os.Parcelable
import com.example.shoppingapp.domain.model.product.ProductChiTiet
import kotlinx.parcelize.Parcelize

@Parcelize
class OrderFromBuyNow(
    var userId: String,
    val sellerId: Int,
    val shopName: String,
    val number: Int,
    val discount: Int,
    val productChiTiet: ProductChiTiet
) : Parcelable