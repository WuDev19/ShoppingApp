package com.example.shoppingapp.domain.usecase.sellerusecase

import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(private val repo: SellerRepository){

    operator fun invoke(otp: String): Flow<StateListener<Unit>> = repo.verifyOtp(otp)

}