package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.local.entity.ProvinceGhnEntity
import com.example.shoppingapp.data.remote.dto.get.ghn.ProvinceGhnDTO
import com.example.shoppingapp.domain.model.ghn.ProvinceGhn

object ProvinceMapper {

    fun mapToProvinceGhnEntity(provinceGhnDTO: ProvinceGhnDTO): ProvinceGhnEntity {
        return ProvinceGhnEntity(provinceGhnDTO.ProvinceID, provinceGhnDTO.ProvinceName)
    }

    fun mapFromDtoToProvinceGhnDomain(provinceGhnDTO: ProvinceGhnDTO): ProvinceGhn {
        return ProvinceGhn(provinceGhnDTO.ProvinceID, provinceGhnDTO.ProvinceName)
    }

    fun mapFromEntityToProvinceGhnDomain(provinceGhnEntity: ProvinceGhnEntity): ProvinceGhn {
        return ProvinceGhn(provinceGhnEntity.provinceID, provinceGhnEntity.provinceName)
    }
}