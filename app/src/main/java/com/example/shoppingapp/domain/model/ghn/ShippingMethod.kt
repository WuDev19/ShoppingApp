package com.example.shoppingapp.domain.model.ghn

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShippingMethod(
    val shopId: Int,
    val feeServiceGhn: FeeServiceGhn,
    val estimateGhn: EstimateGhn,
    var isSelected: Boolean
) : Parcelable