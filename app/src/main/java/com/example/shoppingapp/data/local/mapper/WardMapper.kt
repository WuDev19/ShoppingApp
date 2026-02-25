package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.local.entity.WardGhnEntity
import com.example.shoppingapp.data.remote.dto.get.ghn.WardGhnDTO
import com.example.shoppingapp.domain.model.ghn.WardGhn

object WardMapper {

    fun mapFromDtoToDomain(wardGhnDTO: WardGhnDTO): WardGhn {
        return WardGhn(wardGhnDTO.WardCode, wardGhnDTO.DistrictID, wardGhnDTO.WardName)
    }

    fun mapFromDtoToEntity(wardGhnDTO: WardGhnDTO): WardGhnEntity {
        return WardGhnEntity(wardGhnDTO.WardCode, wardGhnDTO.DistrictID, wardGhnDTO.WardName)
    }

    fun mapFromEntityToDomain(wardGhnEntity: WardGhnEntity): WardGhn {
        return WardGhn(wardGhnEntity.wardCode, wardGhnEntity.districtID, wardGhnEntity.wardName)
    }

}