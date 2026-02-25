package com.example.shoppingapp.domain.usecase.ghn_usecase

import com.example.shoppingapp.domain.model.ghn.DistrictGhn
import com.example.shoppingapp.domain.repository.ghn.GhnRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDistrictOfProvinceUseCase @Inject constructor(private val repo: GhnRepository) {

    operator fun invoke(provinceId: Int): Flow<StateListener<List<DistrictGhn>>> =
        repo.getDistrictOfProvince(provinceId)

}