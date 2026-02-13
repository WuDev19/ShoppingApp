package com.example.shoppingapp.domain.usecase.sellerusecase

import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditProfileSellerUseCase @Inject constructor(private val repo: SellerRepository) {

    operator fun invoke(sellerId: Int, profile: Map<String, String>): Flow<StateListener<Unit>> =
        repo.editProfile(sellerId, profile)

}