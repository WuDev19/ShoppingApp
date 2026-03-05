package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.get.ghn.CalculateFeeGhnDTO
import com.example.shoppingapp.data.remote.dto.get.ghn.EstimateTimeDeliveryDTO
import com.example.shoppingapp.domain.model.ghn.EstimateGhn
import com.example.shoppingapp.domain.model.ghn.FeeServiceGhn

object GhnMapper {

    fun mapFromFeeDtoToFeeServiceGhn(
        calculateFeeGhnDTO: CalculateFeeGhnDTO,
        serviceName: String
    ): FeeServiceGhn {
        return FeeServiceGhn(
            calculateFeeGhnDTO.total,
            calculateFeeGhnDTO.service_fee,
            serviceName,
            calculateFeeGhnDTO.insurance_fee,
            calculateFeeGhnDTO.cod_fee
        )
    }

}