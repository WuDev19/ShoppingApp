package com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase

import com.example.shoppingapp.domain.repository.auth.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val repo: AuthRepository) {

    operator fun invoke(): Flow<StateListener<Boolean>> = repo.logout()

}