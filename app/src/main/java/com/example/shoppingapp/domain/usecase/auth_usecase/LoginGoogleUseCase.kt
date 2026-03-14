package com.example.shoppingapp.domain.usecase.auth_usecase

import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.domain.repository.auth.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginGoogleUseCase @Inject constructor(private val repo: AuthRepository) {

    suspend operator fun invoke (idToken: String) : Flow<StateListener<AuthResponse>>{
        return repo.loginWithGoogle(idToken)
    }

}