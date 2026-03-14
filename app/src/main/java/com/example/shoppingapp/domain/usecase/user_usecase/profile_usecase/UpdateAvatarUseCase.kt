package com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase

import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateAvatarUseCase @Inject constructor(private val repo: UserRepository){

    operator fun invoke(id: String, url: String): Flow<StateListener<String>> = repo.updateAvatar(id, url)

}