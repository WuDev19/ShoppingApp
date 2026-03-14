package com.example.shoppingapp.domain.usecase.auth_usecase

import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserCurrentUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(): Flow<StateListener<String>>{
        return repo.getUserName()
    }

}