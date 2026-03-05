package com.example.shoppingapp.domain.model.ghn

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeeServiceGhn(
    val totalFee: Int,
    val serviceFee: Int,
    val serviceName: String,
    val insuranceFee: Int,
    val codFee: Int
): Parcelable