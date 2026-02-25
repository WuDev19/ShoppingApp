package com.example.shoppingapp.domain.usecase.ghn_usecase

import com.example.shoppingapp.domain.model.ghn.WardGhn
import com.example.shoppingapp.domain.repository.ghn.GhnRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWardOfDistrictUseCase @Inject constructor(private val repo: GhnRepository) {

    operator fun invoke(districtId: Int): Flow<StateListener<List<WardGhn>>> =
        repo.getWardOfDistrict(districtId)

}