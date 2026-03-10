package com.example.shoppingapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class AddressItem (
    val idAddress: Int,
    val name: String,
    val sdt: String,
    val addressName: String,
    val provinceId: Int,
    val districtId: Int,
    val wardCode: String,
    val defaultAddress: Boolean,
    val maND: String,
    var isSelected: Boolean
) : Parcelable