package com.example.shoppingapp.domain.usecase.auth_usecase

import com.example.shoppingapp.domain.model.AuthUser
import com.example.shoppingapp.domain.repository.auth.AuthRepository
import javax.inject.Inject

class CheckLoginUseCase @Inject constructor(private val authRepository : AuthRepository) {
    suspend operator fun invoke() : AuthUser?{
        return authRepository.getCurrentUser()
    }
}