package com.example.shoppingapp.data.remote.dto.get.ghn

import com.example.shoppingapp.domain.model.ghn.ServiceGhn

data class ServiceDTO(
    val service_id: Int,
    val short_name: String,
    val service_type_id: Int,
    val shopGhnId: Int
) {
    fun mapToDomain(): ServiceGhn {
        return ServiceGhn(
            this.service_id,
            this.short_name,
            this.service_type_id,
            this.shopGhnId
        )
    }
}
