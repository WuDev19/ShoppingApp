package com.example.shoppingapp.domain.usecase.sellerusecase

import com.example.shoppingapp.domain.model.OtpModel
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(private val repo: SellerRepository) {
    operator fun invoke(): Flow<StateListener<OtpModel>> = repo.sendOtp()
}