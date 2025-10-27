package com.example.shoppingapp.domain.usecase.authusecase

import com.example.shoppingapp.domain.model.AuthUser
import com.example.shoppingapp.domain.repository.authrepo.AuthRepository
import javax.inject.Inject

class CheckLoginUseCase @Inject constructor(private val authRepository : AuthRepository) {
    suspend operator fun invoke() : AuthUser?{
        return authRepository.getCurrentUser()
    }
}