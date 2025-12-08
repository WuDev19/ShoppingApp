package com.example.shoppingapp.data.remote.dto.get

import com.example.shoppingapp.data.remote.dto.UserDTOBase

data class UserDTOGet(
    override var maND: String? = null,
    override var ten: String? = null,
    override var email: String? = null,
    override var sdt: String? = null,
    var gioitinh: String? = null,
    override var anhdaidien: String? = null,
    var listDiaChi: Set<DiaChiDTO>? = emptySet()
) : UserDTOBase(
    maND,
    ten,
    email,
    sdt,
    anhdaidien
)
