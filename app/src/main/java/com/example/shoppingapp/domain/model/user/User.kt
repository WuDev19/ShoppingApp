package com.example.shoppingapp.domain.model.user

data class User(
    override var maND: String? = null,
    override var ten: String? = null,
    override var email: String? = null,
    var sdt: String? = null,
    var gioitinh: String? = null,
    var anhdaidien: String? = null
) : BaseUser(
    maND,
    ten,
    email
)
