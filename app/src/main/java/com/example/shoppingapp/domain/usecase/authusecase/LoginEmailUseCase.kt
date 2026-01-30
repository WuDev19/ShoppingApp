package com.example.shoppingapp.domain.usecase.authusecase

import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.domain.repository.auth.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginEmailUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Flow<StateListener<AuthResponse>> {
        return authRepository.loginWithEmail(email, password)
    }

}