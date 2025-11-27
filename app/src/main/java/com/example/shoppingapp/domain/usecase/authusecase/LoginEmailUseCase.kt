package com.example.shoppingapp.domain.usecase.authusecase

import com.example.shoppingapp.domain.model.User
import com.example.shoppingapp.domain.repository.authrepo.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginEmailUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Flow<StateListener<AuthResult>> {
        return authRepository.loginWithEmail(email, password)
    }

}