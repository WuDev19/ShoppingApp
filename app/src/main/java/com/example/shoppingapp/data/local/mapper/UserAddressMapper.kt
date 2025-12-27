package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.local.entity.UserAddressEntity
import com.example.shoppingapp.data.remote.dto.get.UserAddressDTO
import com.example.shoppingapp.domain.model.AddressItem

object UserAddressMapper {

    fun mapToAddressItem(userAddressEntity: UserAddressEntity): AddressItem {
        return AddressItem(
            userAddressEntity.idAddress,
            userAddressEntity.name,
            userAddressEntity.sdt,
            userAddressEntity.addressName,
            userAddressEntity.defaultAddress,
            userAddressEntity.maND
        )
    }

    fun mapToUserAddressEntity(userAddressDTO: UserAddressDTO): UserAddressEntity {
        return UserAddressEntity(
            userAddressDTO.idAddress!!,
            userAddressDTO.name!!,
            userAddressDTO.sdt!!,
            userAddressDTO.addressName!!,
            userAddressDTO.defaultAddress!!,
            userAddressDTO.maND!!
        )
    }

}