package com.example.shoppingapp.domain.usecase.seller_usecase

import com.example.shoppingapp.domain.model.SellerSetting
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditProfileSellerUseCase @Inject constructor(private val repo: SellerRepository) {

    operator fun invoke(sellerId: Int, profile: Map<String, String>): Flow<StateListener<SellerSetting>> =
        repo.editProfile(sellerId, profile)

}