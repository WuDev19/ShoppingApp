package com.example.shoppingapp.domain.usecase.ghn_usecase

import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.domain.repository.ghn.GhnRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProvinceUseCase @Inject constructor(private val repo: GhnRepository){

    operator fun invoke(): Flow<StateListener<List<ProvinceGhn>>> = repo.getProvinceGhn()

}