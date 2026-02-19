package com.example.shoppingapp.domain.usecase.sellerusecase

import com.example.shoppingapp.domain.model.SellerSetting
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSellerSettingInfoUseCase @Inject constructor(private val repo: SellerRepository) {

    operator fun invoke(sellerId: Int): Flow<StateListener<SellerSetting>> =
        repo.getSellerSetting(sellerId)

}