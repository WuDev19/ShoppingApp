package com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase

import com.example.shoppingapp.domain.model.user.UserProfile
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(): Flow<StateListener<UserProfile>> = repo.getProfileUser()

}