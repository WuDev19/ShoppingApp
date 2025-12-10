package com.example.shoppingapp.data.remote.dto.post

import com.example.shoppingapp.data.remote.dto.UserDTOBase

data class UserDTOCreate(
    override var maND: String? = null,
    override var ten: String? = null,
    override var email: String? = null,
    override var sdt: String? = null,
    override var anhdaidien: String? = null
): UserDTOBase(
    maND, ten, email, sdt, anhdaidien
)
