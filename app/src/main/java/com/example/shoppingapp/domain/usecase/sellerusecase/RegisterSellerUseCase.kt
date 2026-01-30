package com.example.shoppingapp.domain.usecase.sellerusecase

import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.get.SellerDTO
import com.example.shoppingapp.data.remote.dto.request.SellerRequest
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterSellerUseCase @Inject constructor(private val repo: SellerRepository) {

    operator fun invoke(sellerRequest: SellerRequest): Flow<StateListener<AuthResponse>> =
        repo.registerSeller(sellerRequest)

}