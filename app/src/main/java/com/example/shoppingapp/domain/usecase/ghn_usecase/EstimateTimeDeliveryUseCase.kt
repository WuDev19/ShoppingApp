package com.example.shoppingapp.domain.usecase.ghn_usecase

import com.example.shoppingapp.data.remote.dto.request.ghn.EstimateTimeGhnRequest
import com.example.shoppingapp.domain.model.ghn.EstimateGhn
import com.example.shoppingapp.domain.repository.ghn.GhnRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EstimateTimeDeliveryUseCase @Inject constructor(private val repo: GhnRepository) {

    operator fun invoke(
        estimateTimeGhnRequest: EstimateTimeGhnRequest,
        shopId: Int
    ): Flow<StateListener<EstimateGhn>> = repo.estimateTime(estimateTimeGhnRequest, shopId)

}