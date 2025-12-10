package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.remote.dto.get.UserDTOGet
import com.example.shoppingapp.domain.model.user.BaseUser
import com.example.shoppingapp.domain.model.user.UserProfile

object UserMapper {

    fun mapToBaseUser(userDTOGet: UserDTOGet): BaseUser{
        return BaseUser(
            userDTOGet.maND,
            userDTOGet.ten,
            userDTOGet.email,
            userDTOGet.anhdaidien
        )
    }

    fun mapToUserProfile(userDTOGet: UserDTOGet): UserProfile {
        return UserProfile(
            userDTOGet.maND,
            userDTOGet.ten,
            userDTOGet.email,
            userDTOGet.anhdaidien,
            userDTOGet.sdt,
            userDTOGet.gioitinh
        )
    }

}