package com.example.shoppingapp.domain.usecase.authusecase

import com.example.shoppingapp.domain.model.User
import com.example.shoppingapp.domain.repository.authrepo.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke(
        email: String,
        password: String,
        phone: String,
        username: String
    ): Flow<StateListener<User>> {
        return repository.createAccountWithEmail(email, password, phone, username)
    }

}