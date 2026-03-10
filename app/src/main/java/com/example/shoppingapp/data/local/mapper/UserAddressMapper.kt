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
            userAddressEntity.provinceId,
            userAddressEntity.districtId,
            userAddressEntity.wardCode,
            userAddressEntity.defaultAddress,
            userAddressEntity.maND,
            false
        )
    }

    fun mapToUserAddressEntity(userAddressDTO: UserAddressDTO): UserAddressEntity {
        return UserAddressEntity(
            userAddressDTO.idAddress!!,
            userAddressDTO.name!!,
            userAddressDTO.sdt!!,
            userAddressDTO.addressName!!,
            userAddressDTO.provinceId!!,
            userAddressDTO.districtId!!,
            userAddressDTO.wardCode!!,
            userAddressDTO.defaultAddress!!,
            userAddressDTO.maND!!
        )
    }

}