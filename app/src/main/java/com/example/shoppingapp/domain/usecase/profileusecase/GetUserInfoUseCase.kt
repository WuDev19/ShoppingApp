package com.example.shoppingapp.domain.usecase.profileusecase

import com.example.shoppingapp.domain.model.user.BaseUser
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(id: String): Flow<StateListener<BaseUser>> = repo.getCurrentUser(id)

}