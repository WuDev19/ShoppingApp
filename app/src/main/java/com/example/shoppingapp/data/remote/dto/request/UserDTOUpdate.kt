package com.example.shoppingapp.data.remote.dto.request

import com.example.shoppingapp.data.remote.dto.UserDTOBase

data class UserDTOUpdate(
    override var maND: String? = null,
    override var ten: String? = null,
    override var email: String? = null,
    override var sdt: String? = null,
    override var anhdaidien: String? = null,
    var gioitinh: String? = null
) : UserDTOBase(
    maND, ten, email, sdt, anhdaidien
)
