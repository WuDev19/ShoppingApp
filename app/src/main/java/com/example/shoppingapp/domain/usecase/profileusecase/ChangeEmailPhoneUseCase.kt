package com.example.shoppingapp.domain.usecase.profileusecase

import com.example.shoppingapp.data.remote.dto.post.UserDTOUpdate
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangeEmailPhoneUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>> =
        repo.changeEmailPhoneUser(userDTOUpdate)

}