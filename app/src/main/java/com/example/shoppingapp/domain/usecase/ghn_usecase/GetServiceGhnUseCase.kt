package com.example.shoppingapp.domain.usecase.ghn_usecase

import com.example.shoppingapp.domain.model.ghn.ServiceGhn
import com.example.shoppingapp.domain.repository.ghn.GhnRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServiceGhnUseCase @Inject constructor(private val repo: GhnRepository) {

    operator fun invoke(params: Map<String, String>): Flow<StateListener<ServiceGhn>> =
        repo.getService(params)

}