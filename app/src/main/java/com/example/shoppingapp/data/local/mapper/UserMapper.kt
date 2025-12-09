package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.remote.dto.get.UserDTOGet
import com.example.shoppingapp.domain.model.user.BaseUser

object UserMapper {

    fun mapToBaseUser(userDTOGet: UserDTOGet): BaseUser{
        return BaseUser(
            userDTOGet.maND,
            userDTOGet.ten,
            userDTOGet.email,
            userDTOGet.anhdaidien
        )
    }

}