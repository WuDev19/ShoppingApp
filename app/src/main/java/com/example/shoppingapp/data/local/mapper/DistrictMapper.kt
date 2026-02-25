package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.local.entity.DistrictGhnEntity
import com.example.shoppingapp.data.remote.dto.get.ghn.DistrictGhnDTO
import com.example.shoppingapp.domain.model.ghn.DistrictGhn

object DistrictMapper {

    fun mapFromDtoToDomain(districtGhnDTO: DistrictGhnDTO): DistrictGhn {
        return DistrictGhn(
            districtGhnDTO.DistrictID,
            districtGhnDTO.ProvinceID,
            districtGhnDTO.DistrictName
        )
    }

    fun mapFromDtoToEntity(districtGhnDTO: DistrictGhnDTO): DistrictGhnEntity {
        return DistrictGhnEntity(
            districtGhnDTO.DistrictID,
            districtGhnDTO.ProvinceID,
            districtGhnDTO.DistrictName
        )
    }

    fun mapFromEntityToDomain(districtGhnEntity: DistrictGhnEntity): DistrictGhn {
        return DistrictGhn(
            districtGhnEntity.districtID,
            districtGhnEntity.provinceID,
            districtGhnEntity.districtName
        )
    }

}