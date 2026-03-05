package com.example.shoppingapp.domain.model.ghn

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EstimateGhn (
    val from: String,
    val to: String
): Parcelable