package com.example.shoppingapp.domain.usecase.ghn_usecase

import com.example.shoppingapp.data.remote.dto.request.ghn.CalculateFeeGhnRequest
import com.example.shoppingapp.domain.model.ghn.FeeServiceGhn
import com.example.shoppingapp.domain.model.ghn.ServiceGhn
import com.example.shoppingapp.domain.repository.ghn.GhnRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// TODO: refractor model
class CalculateFeeUseCase @Inject constructor(private val repo: GhnRepository) {

    operator fun invoke(
        calculateFeeGhnRequest: CalculateFeeGhnRequest,
        shopId: Int,
        serviceGhn: ServiceGhn,
    ): Flow<StateListener<FeeServiceGhn>> {
        calculateFeeGhnRequest.service_id = serviceGhn.serviceId
        calculateFeeGhnRequest.service_type_id = serviceGhn.serviceTypeId
        return repo.calculateFee(calculateFeeGhnRequest, shopId, serviceGhn.serviceName)
    }

}