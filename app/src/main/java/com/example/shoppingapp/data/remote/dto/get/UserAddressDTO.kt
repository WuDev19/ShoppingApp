package com.example.shoppingapp.data.remote.dto.get

data class UserAddressDTO(
    val idAddress: Int? = 0,
    val name: String? = "",
    val sdt: String? = "",
    val addressName: String? = "",
    val defaultAddress: Boolean? = false,
    val maND: String? = ""
)
