package com.example.shoppingapp.domain.usecase.auth_usecase

import com.example.shoppingapp.domain.model.LoginModel
import com.example.shoppingapp.domain.repository.auth.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke(
        email: String,
        password: String,
        phone: String,
        username: String
    ): Flow<StateListener<LoginModel>> {
        return repository.createAccountWithEmail(email, password, phone, username)
    }

}