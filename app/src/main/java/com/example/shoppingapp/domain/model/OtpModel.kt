package com.example.shoppingapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OtpModel(
    val expireTime: Int,
    val message: String
): Parcelable