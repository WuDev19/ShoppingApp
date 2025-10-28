package com.example.shoppingapp.domain.usecase.authusecase

import com.example.shoppingapp.domain.repository.authrepo.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val auth: AuthRepository) {

    suspend operator fun invoke(email: String) : Flow<StateListener<Void>> {
        return auth.resetPassword(email)
    }

}